package gg.projecteden.nexus.features.resourcepack.customblocks.customblockbreaking;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocksLang;
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
		CustomBlocksLang.debug(event.getPlayer(), "CustomBlockBreaking: BlockBreakEvent");

		if (event.isCancelled()) {
			CustomBlocksLang.debug(event.getPlayer(), "<-- event is cancelled");
			return;
		}

		if (!CustomBlockBreaking.getManager().isTracking(event.getBlock())) {
			CustomBlocksLang.debug(event.getPlayer(), "<-- already tracking");
			return;
		}

		CustomBlockBreaking.getManager().removeBrokenBlock(event.getBlock());
	}

	@EventHandler
	public void on(BlockDamageEvent event) {
		Player player = event.getPlayer();
		CustomBlocksLang.debug(player, "CustomBlockBreaking: BlockDamageEvent");
		if (event.isCancelled()) {
			CustomBlocksLang.debug(player, "<-- event is cancelled");
			return;
		}


		if (isInvalid(player)) {
			CustomBlocksLang.debug(player, "<-- player is invalid");
			return;
		}

		// 6 tick delay after breaking a block, before able to damage another
		int currentTick = Bukkit.getCurrentTick();
		if (breakWait.containsKey(player.getUniqueId())) {
			if (currentTick < (6 + breakWait.get(player.getUniqueId()))) {
				CustomBlocksLang.debug(player, "<-- on cooldown");
				return;
			}
		}

		Block block = event.getBlock();
		if (CustomBlockBreaking.getManager().isTracking(block)) {
			CustomBlocksLang.debug(player, "<-- already tracking");
			return;
		}

		ItemStack itemInHand = event.getItemInHand();
		CustomBlockBreaking.getManager().createBrokenBlock(block, player, itemInHand);
	}

	@EventHandler
	public void on(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		CustomBlocksLang.debug(player, "CustomBlockBreaking: PlayerAnimationEvent");
		if (isInvalid(player)) {
			CustomBlocksLang.debug(player, "<-- player is invalid");
			return;
		}

		Block block = player.getTargetBlockExact(5);
		if (block == null || blackListed.contains(block.getType())) {
			CustomBlocksLang.debug(player, "<-- block == null || block is blacklisted");
			return;
		}

		Location blockLoc = block.getLocation();
		if (player.getLocation().distanceSquared(blockLoc) >= 1024.0D) {
			CustomBlocksLang.debug(player, "<-- player is too far away");
			return;
		}

		if (!CustomBlockBreaking.getManager().isTracking(blockLoc)) {
			CustomBlocksLang.debug(player, "<-- already tracking");
			return;
		}

		BrokenBlock brokenBlock = CustomBlockBreaking.getManager().getBrokenBlock(blockLoc);
		if (brokenBlock == null) {
			CustomBlocksLang.debug(player, "<-- broken block is null");
			return;
		}

		BlockBreakingUtils.addSlowDig(player, 10);
		brokenBlock.incrementDamage(player, player.getInventory().getItemInMainHand());
	}
}
