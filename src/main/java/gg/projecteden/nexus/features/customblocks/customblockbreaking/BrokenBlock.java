package gg.projecteden.nexus.features.customblocks.customblockbreaking;

import gg.projecteden.nexus.utils.NMSUtils;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

@Data
public class BrokenBlock {
	@Getter
	private static final DecimalFormat df = new DecimalFormat("#.###");

	private Block block;
	private int damageFrame = 0;
	private int initialDamageTick;
	private int lastDamageTick;

	public BrokenBlock(Block block) {
		this.block = block;
		this.initialDamageTick = Bukkit.getCurrentTick();
		this.lastDamageTick = Bukkit.getCurrentTick();
	}

	public void breakBlock(Player breaker) {
		resetBreakPacket();
		CustomBlockBreaking.getManager().removeBrokenBlock(this);

		if (breaker != null)
			BlockBreakingUtils.sendBreakBlock(breaker, this.block);
	}

	public void resetBreakPacket() {
		sendBreakPacket(-1);
	}

	public void sendBreakPacket(int animation) {
		BlockBreakingUtils.sendBreakPacket(animation, this.block);
	}

	public void incrementDamage(Player player, ItemStack itemStack) {
		double breakTicks = Math.ceil(1 / NMSUtils.getBlockDamage(player, block, itemStack));
		double currentTicks = Bukkit.getCurrentTick() - getInitialDamageTick();

		this.damageFrame = (int) Math.round(currentTicks / breakTicks);
		sendBreakPacket(this.damageFrame);

		if (currentTicks >= breakTicks) {
			breakBlock(player);
			CustomBlockBreaking.getManager().removeBrokenBlock(this);
		}
	}

	public void decrementDamage() {
		double currentTicks = Bukkit.getCurrentTick() - getLastDamageTick();
		if (currentTicks < 10)
			return;

		this.damageFrame--;
		sendBreakPacket(this.damageFrame);
	}

	public boolean isBroken() {
		return getDamageFrame() == 10;
	}

	public boolean isDamaged() {
		return this.damageFrame > 0;
	}
}
