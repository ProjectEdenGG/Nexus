package gg.projecteden.nexus.features.resourcepack.customblocks.customblockbreaking;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.utils.Debug.DebugType;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BreakListener implements Listener {

	@Getter
	private static final HashMap<UUID, Integer> breakWait = new HashMap<>();

	private static final Set<Material> blackListed = new HashSet<>();

	public BreakListener() {
		Nexus.registerListener(this);

		blackListed.addAll(Set.of(Material.BARRIER, Material.LIGHT));
		blackListed.addAll(MaterialTag.LIQUIDS.getValues());
		blackListed.addAll(MaterialTag.ALL_AIR.getValues());
	}

	private boolean isInvalid(Player player) {
		return GameModeWrapper.of(player).isCreative();
	}

	@EventHandler
	public void on(BlockBreakEvent event) {
		CustomBlockUtils.debug(event.getPlayer(), DebugType.CUSTOM_BLOCK_DAMAGE, "CustomBlockBreaking: BlockBreakEvent");

		if (event.isCancelled()) {
			CustomBlockUtils.debug(event.getPlayer(), DebugType.CUSTOM_BLOCK_DAMAGE, "<-- event is cancelled");
			return;
		}

		if (!CustomBlockBreaking.isTracking(event.getBlock())) {
			CustomBlockUtils.debug(event.getPlayer(), DebugType.CUSTOM_BLOCK_DAMAGE, "<-- already tracking");
			return;
		}

		CustomBlockBreaking.removeBrokenBlock(event.getBlock().getLocation());
	}

	@EventHandler
	public void on(BlockDamageEvent event) {
		Player player = event.getPlayer();
		CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCK_DAMAGE, "CustomBlockBreaking: BlockDamageEvent");
		if (event.isCancelled()) {
			CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCK_DAMAGE, "<-- event is cancelled");
			return;
		}

		if (isInvalid(player)) {
			CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCK_DAMAGE, "<-- player is invalid");
			return;
		}

		// 6 tick delay after breaking a block, before able to damage another
		int currentTick = Bukkit.getCurrentTick();
		if (breakWait.containsKey(player.getUniqueId())) {
			if (currentTick < (6 + breakWait.get(player.getUniqueId()))) {
				CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCK_DAMAGE, "<-- on cooldown");
				return;
			}
		}

		Block block = event.getBlock();
		if (CustomBlockBreaking.isTracking(block)) {
			CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCK_DAMAGE, "<-- already tracking");
			return;
		}

		ItemStack itemInHand = event.getItemInHand();
		CustomBlockBreaking.createBrokenBlock(block, player, itemInHand);
	}

	@EventHandler
	public void on(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (isInvalid(player))
			return;

		Block block = player.getTargetBlockExact(5);
		if (block == null || blackListed.contains(block.getType()))
			return;

		Location blockLoc = block.getLocation();
		if (player.getLocation().distanceSquared(blockLoc) >= 1024.0D)
			return;

		if (!CustomBlockBreaking.isTracking(blockLoc))
			return;

		BrokenBlock brokenBlock = CustomBlockBreaking.getBrokenBlock(blockLoc);
		if (brokenBlock == null)
			return;

		CustomBlockBreaking.addSlowDig(player, 10);
		brokenBlock.incrementDamage(player, player.getInventory().getItemInMainHand());
	}

	@EventHandler
	public void on(BlockDamageAbortEvent event) {
		Block block = event.getBlock();
		if (blackListed.contains(block.getType()))
			return;

		Location blockLoc = block.getLocation();
		if (!CustomBlockBreaking.isTracking(blockLoc))
			return;

		BrokenBlock brokenBlock = CustomBlockBreaking.getBrokenBlock(blockLoc);
		if (brokenBlock == null)
			return;

		brokenBlock.resetDamagePacket();
		brokenBlock.remove();
	}
}
