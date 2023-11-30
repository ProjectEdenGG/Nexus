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

	private Player player;
	private Location location;
	private Block block;
	private boolean isCustomBlock;

	private ItemStack initialItemStack;
	private int breakTicks;
	private int damageFrame = 0;
	private int lastDamageTick;
	private int totalDamageTicks = 0;

	public BrokenBlock(Block block, boolean isCustomBlock, Player player, ItemStack itemStack, int currentTick) {
		this.player = player;
		this.location = block.getLocation();
		this.block = block;
		this.isCustomBlock = isCustomBlock;
		this.initialItemStack = itemStack;

		float blockDamage = getBlockDamage(itemStack);
		if (blockDamage <= 0)
			this.breakTicks = 1;
		else
			this.breakTicks = (int) Math.ceil(1 / blockDamage);

		this.lastDamageTick = currentTick;
	}

	public Block getBlock() {
		return location.getBlock();
	}

	public float getBlockDamage(ItemStack tool) {
		if (isCustomBlock) {
//			debug("CustomBlock getBlockDamage");
			return getCustomBlock().get().getBlockDamage(this.player, tool);
		}

//		debug("Vanilla getBlockDamage");
		return BlockUtils.getBlockDamage(this.player, tool, this.block);
	}

	private CustomBlock getCustomBlock() {
		if (!isCustomBlock)
			return null;

		return CustomBlock.fromBlock(block);
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

		float blockDamage = getBlockDamage(itemStack);
		if (blockDamage <= 0.0)
			this.breakTicks = 1;
		else
			this.breakTicks = (int) Math.ceil(1 / blockDamage);
	}

	public void breakBlock(@NonNull Player breaker) {
		BlockBreakingUtils.sendBreakBlock(breaker, getBlock(), getCustomBlock());
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

		this.damageFrame = (int) Math.round(((double) this.totalDamageTicks / this.breakTicks) * 10.0);
		sendDamagePacket(this.damageFrame);

		this.totalDamageTicks++;

		if (this.totalDamageTicks >= this.breakTicks) {
			breakBlock(player);
		}
	}

	public void decrementDamage(int currentTick) {
		this.lastDamageTick = currentTick;

		this.damageFrame--;
		sendDamagePacket(this.damageFrame);

		this.totalDamageTicks -= Math.round(this.breakTicks / 10.0);

		if (this.totalDamageTicks < 0) {
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
