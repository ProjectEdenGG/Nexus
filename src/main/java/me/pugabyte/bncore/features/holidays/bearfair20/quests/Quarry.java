package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.*;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.miningError;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.toolError;
import static me.pugabyte.bncore.utils.Utils.getTool;

public class Quarry implements Listener {

	public static void shutdown() {
		Set<Location> locations = new HashSet<>(dioriteRegenMap.keySet());
		for (Location loc : locations) {
			Diorite diorite = dioriteRegenMap.get(loc);
			loc.getBlock().setType(diorite.getType());
			loc.getBlock().setBlockData(diorite.getBlockData());
			dioriteRegenMap.remove(loc);
		}
	}

	@Data
	@AllArgsConstructor
	private static class Diorite {
		@NonNull
		Material type;
		BlockData blockData;
	}

	public static String quarryRg = BearFair20.getRegion() + "_main_quarry";
	private static List<Material> diorite = Arrays.asList(Material.DIORITE, Material.DIORITE_SLAB, Material.DIORITE_STAIRS, Material.DIORITE_WALL);
	private static Map<Location, Diorite> dioriteRegenMap = new HashMap<>();

	public Quarry() {
		BNCore.registerListener(this);
		regenTasks();
	}

	private void regenTasks() {
		Tasks.repeat(0, Time.SECOND.x(10), () -> {
			Set<Location> locations = new HashSet<>(dioriteRegenMap.keySet());
			for (Location loc : locations) {
				Block air = loc.getBlock();
				Diorite diorite = dioriteRegenMap.get(loc);

				if (Utils.chanceOf(20)) {
					air.setType(diorite.getType());
					air.setBlockData(diorite.getBlockData());
					dioriteRegenMap.remove(loc);
				}
			}
		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMineDiorite(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (event.isCancelled()) return;
		if (!isAtBearFair(player)) return;
		if (player.hasPermission("worldguard.region.bypass.*")) {
			if (player.getInventory().getItemInMainHand().getType().equals(Material.NETHER_BRICK))
				return;
		}

		// If you mined a block in the quarry that has an adj block that needs support
		if (adjBlockNeedsSupport(block) && isInRegion(block, quarryRg)) {
			event.setCancelled(true);
			return;
		}

		// If you mined diorite
		if (!diorite.contains(block.getType())) {
			if (isInRegion(block, quarryRg))
				event.setCancelled(true);
			return;
		}

		// if your in the quarry
		if (!isInRegion(block, quarryRg)) {
			event.setCancelled(true);
			send(miningError, player);
			return;
		}

		// If the tool is a bf20 item
		ItemStack tool = getTool(player);
		if (!isBFItem(tool)) {
			event.setCancelled(true);
			send(toolError, player);
			return;
		}

		Diorite diorite = new Diorite(block.getType(), block.getBlockData());
		Tasks.wait(Time.SECOND.x(3), () -> dioriteRegenMap.put(block.getLocation(), diorite));
	}


	private boolean adjBlockNeedsSupport(Block origin) {
		List<Block> adj = Utils.getAdjacentBlocks(origin);
		if (adj.size() == 0)
			return false;
		for (Block block : adj) {
			if (MaterialTag.NEEDS_SUPPORT.isTagged(block.getType()))
				return true;
		}
		return false;
	}
}
