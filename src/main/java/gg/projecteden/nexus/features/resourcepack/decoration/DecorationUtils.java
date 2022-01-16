package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class DecorationUtils {

	private static final List<BlockFace> hitboxDirections = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
	public static boolean debug = false;

	public static void error(Player player) {
		error(player, "&c&lHey! &7Sorry, but you can't use that here.");
	}

	public static void error(Player player, String error) {
		PlayerUtils.send(player, error);
	}

	// Pathway
	private static Set<Block> getConnectedHitboxes(HitboxMaze maze) {
		maze.incrementTries();
		if (maze.getTries() > 1000) {
			debug("MAX TRIES");
			return maze.getFound();
		}

		if (maze.getDirectionsLeft().isEmpty()) {
			maze.setBlock(maze.getPath().getLast());
			if (maze.getBlock().getLocation().equals(maze.getOrigin().getLocation())) {
				debug("origin == block, ending");
				return maze.getFound();
			}

			return getConnectedHitboxes(maze);
		}

		debug("Dirs Left: " + maze.getDirectionsLeft());

		maze.setBlockFace(maze.getDirectionsLeft().get(0));
		debug("Dir: " + maze.getBlockFace());

		maze.getDirectionsLeft().remove(maze.getBlockFace());

		Block relative = maze.getBlock().getRelative(maze.getBlockFace());
		debug("Type: " + relative.getType());

		double distance = maze.getOrigin().getLocation().distance(relative.getLocation());
		Set<Material> hitboxTypes = DecorationType.getHitboxTypes();
		if (maze.getTried().contains(relative) || !hitboxTypes.contains(relative.getType()) || distance > 6) {

			if (!hitboxTypes.contains(relative.getType()))
				debug("Type not a hitbox");
			else if (distance > 6)
				debug("distance > 6");

			maze.getTried().add(relative);
			maze.setBlock(maze.getPath().getLast());

			debug("Removing Dir: " + maze.getBlockFace());
			return getConnectedHitboxes(maze);
		}

		debug("Found: " + StringUtils.getShortLocationString(relative.getLocation()));
		maze.getFound().add(relative);
		maze.getTried().add(relative);
		maze.setDirectionsLeft(new ArrayList<>(hitboxDirections));

		return getConnectedHitboxes(maze);
	}

	static ItemFrame findItemFrame(Set<Block> connectedHitboxes, Block clicked) {
		Location clickedLoc = clicked.getLocation();

		Map<Location, HitboxData> dataMap = new HashMap<>();
		for (Block block : connectedHitboxes) {
			ItemFrame itemFrame = block.getLocation().toCenterLocation().getNearbyEntitiesByType(ItemFrame.class, 0.5).stream().findFirst().orElse(null);
			if (itemFrame == null)
				continue;

			ItemStack itemStack = itemFrame.getItem();
			if (isNullOrAir(itemStack))
				continue;

			DecorationType type = DecorationType.of(itemStack);
			if (type == null)
				continue;

			if (dataMap.containsKey(block.getLocation()))
				continue;

			debugDot(block.getLocation(), Color.PURPLE);

			HitboxData hitboxData = new HitboxData.HitboxDataBuilder()
				.itemFrame(itemFrame)
				.block(block)
				.decorationType(type)
				.build();

			dataMap.put(block.getLocation(), hitboxData);
		}

		for (HitboxData hitboxData : dataMap.values()) {
			Decoration decoration = hitboxData.getDecorationType().getDecoration();
			List<Hitbox> hitboxes = decoration.getHitboxes(hitboxData.getItemFrame());

			Block block = hitboxData.getBlock();
			if (block == null)
				continue;

			debug("Checking hitboxes for " + StringUtils.camelCase(hitboxData.getDecorationType()));
			for (Hitbox hitbox : hitboxes) {
				Block _block = block;

				Map<BlockFace, Integer> offsets = hitbox.getOffsets();
				if (!offsets.isEmpty())
					for (BlockFace blockFace : offsets.keySet())
						_block = _block.getRelative(blockFace, offsets.get(blockFace));

				debugDot(_block.getLocation(), Color.WHITE);
				debug(StringUtils.getShortLocationString(_block.getLocation()) + " == "
					+ StringUtils.getShortLocationString(clickedLoc));

				if (LocationUtils.isFuzzyEqual(_block.getLocation(), clickedLoc)) {
					debug("found correct decoration");
					debugDot(_block.getLocation(), Color.AQUA);
					return hitboxData.getItemFrame();
				}
			}
		}

		return null;
	}

	@Nullable
	static ItemFrame getItemFrame(Block clicked) {
		if (isNullOrAir(clicked))
			return null;

		Set<Material> hitboxTypes = DecorationType.getHitboxTypes();
		if (!hitboxTypes.contains(clicked.getType()))
			return null;

		// Single
		ItemFrame itemFrame = clicked.getLocation().toCenterLocation().getNearbyEntitiesByType(ItemFrame.class, 0.5).stream().findFirst().orElse(null);
		if (itemFrame != null) {
			ItemStack itemStack = itemFrame.getItem();
			if (!isNullOrAir(itemStack)) {
				DecorationType type = DecorationType.of(itemStack);
				if (type != null) {
					debug("Single");
					return itemFrame;
				}
			}
		}

		// Multi
		Set<Block> connectedHitboxes = getConnectedHitboxes(new HitboxMaze(clicked));
		debug("Connected Hitboxes: " + connectedHitboxes.size());

		return findItemFrame(connectedHitboxes, clicked);
	}

	static void debug(String message) {
		debug(Dev.WAKKA.getPlayer(), message);
	}

	static void debug(Player player, String message) {
		if (debug)
			PlayerUtils.send(player, message);
	}

	private static void debugDot(Location location, Color color) {
		if (debug)
			DotEffect.debug(Dev.WAKKA.getPlayer(), location.clone().toCenterLocation(), color);
	}

	@Data
	@AllArgsConstructor
	static class HitboxMaze {
		Block origin;
		Block block;
		BlockFace blockFace;
		List<BlockFace> directionsLeft = new ArrayList<>(hitboxDirections);
		Set<Block> found = new HashSet<>();
		LinkedList<Block> path = new LinkedList<>();
		Set<Block> tried = new HashSet<>();
		int tries = 0;

		public HitboxMaze(Block clicked) {
			origin = clicked;
			block = clicked;
			blockFace = BlockFace.NORTH;
			path.add(origin);

		}

		public void incrementTries() {
			++this.tries;
		}
	}
}
