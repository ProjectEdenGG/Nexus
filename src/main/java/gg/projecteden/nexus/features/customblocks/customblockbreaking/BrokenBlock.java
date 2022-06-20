package gg.projecteden.nexus.features.customblocks.customblockbreaking;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

@Data
public class BrokenBlock {
	@Getter
	private static final DecimalFormat df = new DecimalFormat("#.###");

	private Location location;
	private Object blockObject;

	private ItemStack initialItemStack;
	private int breakTicks;
	private int damageFrame = 0;
	private int lastDamageTick;
	private int totalDamageTicks = 0;

	public BrokenBlock(Location location, Object blockObject, Player player, ItemStack itemStack, int currentTick) {
		this.location = location;
		this.blockObject = blockObject;
		this.initialItemStack = itemStack;

		float blockDamage = getBlockDamage(player, blockObject, itemStack);
		if (blockDamage <= 0.0)
			this.breakTicks = 1;
		else
			this.breakTicks = (int) Math.ceil(1 / blockDamage);

		this.lastDamageTick = currentTick;
	}

	public Block getBlock() {
		return location.getBlock();
	}

	private float getBlockDamage(Player player, Object blockObject, ItemStack itemStack) {
		if (blockObject instanceof Block block) {
			return BlockUtils.getBlockDamage(player, itemStack, block);
		}

		if (blockObject instanceof CustomBlock customBlock) {
			return customBlock.get().getBlockDamage(player, itemStack);
		}

		return 0.0F;
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

	public void breakBlock(@NonNull Player breaker) {
		BlockBreakingUtils.sendBreakBlock(breaker, getBlock(), blockObject);
	}

	public void resetDamagePacket() {
		sendDamagePacket(-1);
	}

	public void sendDamagePacket(int frame) {
		BlockBreakingUtils.sendBreakPacket(frame, getBlock());
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

//		debug("Ticks = " + this.totalDamageTicks + " / " + this.breakTicks +" | Frame = " + this.damageFrame);

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
