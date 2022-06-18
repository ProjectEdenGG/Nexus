package gg.projecteden.nexus.features.customblocks.customblockbreaking;

import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
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
	private ItemStack initialItemStack;
	private int breakTicks;
	private int damageFrame = 0;
	private int lastDamageTick;
	private int totalDamageTicks = 0;

	public BrokenBlock(Block block, Player player, ItemStack itemStack, int currentTick) {
		this.block = block;
		this.initialItemStack = itemStack;

		float blockDamage = NMSUtils.getBlockDamage(player, block, itemStack);
		if (blockDamage <= 0.0)
			this.breakTicks = 1;
		else
			this.breakTicks = (int) Math.ceil(1 / blockDamage);

		Dev.WAKKA.send("Break Ticks = " + breakTicks);

		this.lastDamageTick = currentTick;
	}

	public void remove() {
		CustomBlockBreaking.getManager().removeBrokenBlock(this);
	}

	public void reset(ItemStack itemStack, int currentTick) {
		resetDamagePacket();
		this.damageFrame = 0;
		this.totalDamageTicks = 0;
		this.initialItemStack = itemStack;
		this.lastDamageTick = currentTick;
	}

	public void breakBlock(Player breaker) {
		remove();

		if (breaker != null)
			BlockBreakingUtils.sendBreakBlock(breaker, this.block);
	}

	public void resetDamagePacket() {
		sendDamagePacket(-1);
	}

	public void sendDamagePacket(int frame) {
		BlockBreakingUtils.sendBreakPacket(frame, this.block);
	}

	public void incrementDamage(Player player, ItemStack itemStack) {
		int currentTick = Bukkit.getCurrentTick();
		if (!ItemUtils.isFuzzyMatch(itemStack, this.initialItemStack)) {
			reset(itemStack, currentTick);
			return;
		}

		this.lastDamageTick = currentTick;
		this.totalDamageTicks++;

		this.damageFrame = (int) Math.round(((double) this.totalDamageTicks / this.breakTicks) * 10.0);
		sendDamagePacket(this.damageFrame);

//		Dev.WAKKA.send("Ticks = " + this.totalDamageTicks + " / " + this.breakTicks +" | Frame = " + this.damageFrame);

		if (this.totalDamageTicks >= this.breakTicks) {
			breakBlock(player);
		}
	}

	public void decrementDamage(int currentTick) {
		this.lastDamageTick = currentTick;
		this.totalDamageTicks -= (this.breakTicks / 10);

		this.damageFrame--;
		sendDamagePacket(this.damageFrame);

		if (this.totalDamageTicks <= 0) {
			resetDamagePacket();
			remove();
		}
	}

	public boolean isBroken() {
		return getDamageFrame() == 10;
	}

	public boolean isDamaged() {
		return this.damageFrame >= 0;
	}
}
