package gg.projecteden.nexus.features.mcmmo.mastery.skills.woodcutting;

import com.gmail.nossr50.util.EventUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AutoReplantSaplings implements Listener {
	public static final String PERMISSION = "nexus.mcmmo.woodcutting.auto_replant_saplings";

	private void replant(Player player, Block trunk, Material sapling) {
		if (!trunk.getType().isAir())
			return;

		BlockData planting = sapling.createBlockData();
		if (!trunk.canPlace(planting))
			return;

		PlayerInventory inventory = player.getInventory();
		int slot = inventory.first(sapling);
		if (slot < 0)
			return;

		ItemStack seed = inventory.getItem(slot);
		if (seed == null)
			return;

		BlockState replaced = trunk.getState();
		Block soil = trunk.getRelative(BlockFace.DOWN);
		trunk.setBlockData(planting, false);

		BlockPlaceEvent place = new BlockPlaceEvent(trunk, replaced, soil, seed.asOne(), player, true, EquipmentSlot.HAND);
		place.callEvent();

		ItemStack remaining = inventory.getItem(slot);
		if (place.isCancelled() || !place.canBuild() || remaining == null || !remaining.isSimilar(seed)) {
			if (trunk.getType() == sapling)
				replaced.update(true, false);
			return;
		}

		if (trunk.getType() != sapling)
			return;

		if (remaining.getAmount() == 1)
			inventory.clear(slot);
		else
			remaining.setAmount(remaining.getAmount() - 1);
	}

	private static Material saplingFor(Material log) {
		return switch (log) {
			case OAK_LOG -> Material.OAK_SAPLING;
			case SPRUCE_LOG -> Material.SPRUCE_SAPLING;
			case BIRCH_LOG -> Material.BIRCH_SAPLING;
			case JUNGLE_LOG -> Material.JUNGLE_SAPLING;
			case ACACIA_LOG -> Material.ACACIA_SAPLING;
			case DARK_OAK_LOG -> Material.DARK_OAK_SAPLING;
			case CHERRY_LOG -> Material.CHERRY_SAPLING;
			case PALE_OAK_LOG -> Material.PALE_OAK_SAPLING;
			case MANGROVE_LOG -> Material.MANGROVE_PROPAGULE;
			default -> null;
		};
	}

	private static final int MAX_TREE_LOGS = 256;

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (EventUtils.isFakeEvent(event))
			return;

		Player player = event.getPlayer();
		if (!player.hasPermission(PERMISSION)) return;
		if (player.getGameMode() == GameMode.CREATIVE) return;
		if (!Tag.ITEMS_AXES.isTagged(player.getInventory().getItemInMainHand().getType())) return;

		Block broken = event.getBlock();
		Material sapling = saplingFor(broken.getType());
		if (sapling == null) return;

		List<Block> plantingSpots = findPlantingSpots(broken);
		if (plantingSpots.isEmpty()) return;

		Tasks.wait(1, () -> {
			if (event.isCancelled()) return;
			if (!player.isOnline()) return;
			if (!player.getWorld().equals(broken.getWorld())) return;
			if (!player.hasPermission(PERMISSION)) return;

			for (Block spot : plantingSpots)
				replant(player, spot, sapling);
		});
	}

	private static List<Block> findPlantingSpots(Block start) {
		Material logType = start.getType();
		Material leafType = Material.valueOf(
			logType.name().replace("_LOG", "_LEAVES")
		);

		Set<Block> logs = new HashSet<>();
		ArrayDeque<Block> pending = new ArrayDeque<>();
		List<Block> plantingSpots = new ArrayList<>();
		boolean hasCanopy = false;

		logs.add(start);
		pending.add(start);

		while (!pending.isEmpty()) {
			Block log = pending.removeFirst();
			if (!Tag.LOGS.isTagged(log.getRelative(BlockFace.DOWN).getType()))
				plantingSpots.add(log);

			// Include diagonal branches and adjacent columns of 2×2 trunks.
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					for (int dz = -1; dz <= 1; dz++) {
						if (dx == 0 && dy == 0 && dz == 0)
							continue;

						Block neighbor = log.getRelative(dx, dy, dz);
						if (!log.getWorld().isChunkLoaded(
							neighbor.getX() >> 4, neighbor.getZ() >> 4))
							continue;

						Material type = neighbor.getType();
						if (type == logType && logs.add(neighbor)) {
							if (logs.size() > MAX_TREE_LOGS)
								return List.of();
							pending.addLast(neighbor);
						} else if (!hasCanopy && type == leafType
							&& neighbor.getBlockData() instanceof Leaves leaves
							&& !leaves.isPersistent()) {
							hasCanopy = true;
						}
					}
				}
			}
		}

		return hasCanopy ? plantingSpots : List.of();
	}

}
