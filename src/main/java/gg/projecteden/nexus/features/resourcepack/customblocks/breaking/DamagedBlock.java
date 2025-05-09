package gg.projecteden.nexus.features.resourcepack.customblocks.breaking;

import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Debug.DebugType;
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
public class DamagedBlock {
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

	public DamagedBlock(Block block, Player player, ItemStack itemStack) {
		this(block, CustomBlock.from(block) != null, player, itemStack, Bukkit.getCurrentTick());
	}

	public DamagedBlock(Block block, boolean isCustomBlock, Player player, ItemStack itemStack, int currentTick) {
		this.player = player;
		this.location = block.getLocation();
		this.block = block;
		this.isCustomBlock = isCustomBlock;
		this.initialItemStack = itemStack;

		float blockDamage = getBlockDamage(itemStack);
		if (blockDamage < 0)
			this.breakTicks = Integer.MAX_VALUE;
		else if (blockDamage == 0)
			this.breakTicks = 1;
		else
			this.breakTicks = (int) Math.ceil(1 / blockDamage);

		this.lastDamageTick = currentTick;
	}

	public Block getBlock() {
		return location.getBlock();
	}

	public static double getBlockHardness(Block block) {
		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock != null) {
			return customBlock.get().getBlockHardness();
		}

		return BlockUtils.getBlockHardness(block);
	}

	public static float getBlockDamage(Player player, ItemStack tool, Block block) {
		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock != null) {
			Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "CustomBlock getBlockDamage:");
			return customBlock.get().getBlockDamage(player, tool);
		}

		Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "Vanilla getBlockDamage:");
		return BlockUtils.getBlockDamage(player, tool, block);
	}

	public float getBlockDamage(ItemStack tool) {
		return getBlockDamage(this.player, tool, this.block);
	}

	private CustomBlock getCustomBlock() {
		if (!isCustomBlock)
			return null;

		return CustomBlock.from(block);
	}

	public void remove() {
		resetDamagePacket();
		Breaker.stopTracking(this.getLocation());
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
		CustomBlockUtils.debug(breaker, "Breaking block...");
		BreakListener.getBreakWait().put(breaker.getUniqueId(), Bukkit.getCurrentTick());
		Breaker.sendBreakBlock(breaker, getBlock(), getCustomBlock());
		remove();
	}

	public void resetDamagePacket() {
		sendDamagePacket(-1);
	}

	public void sendDamagePacket(int frame) {
		Breaker.sendBreakPacket(frame, getBlock());
	}

	public void incrementDamage(Player player, ItemStack itemStack) {
		CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCK_DAMAGE, "Incrementing damage...");
		int currentTick = Bukkit.getCurrentTick();
		if (!ItemUtils.isFuzzyMatch(itemStack, this.initialItemStack)) {
			CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCK_DAMAGE, "<-- using different tool, resetting progress");
			reset(itemStack, currentTick);
			return;
		}

		if (this.lastDamageTick == currentTick)
			return;

		this.lastDamageTick = currentTick;

		this.damageFrame = (int) Math.round(((double) this.totalDamageTicks / this.breakTicks) * 10.0);
		if (this.breakTicks == 1)
			this.damageFrame = 10;

		CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCK_DAMAGE, "Damage frame: " + this.damageFrame + " | Damage tick: " + this.lastDamageTick);
		sendDamagePacket(this.damageFrame);

		this.totalDamageTicks++;

		if (this.totalDamageTicks >= this.breakTicks) {
			breakBlock(player);
		}
	}
}
