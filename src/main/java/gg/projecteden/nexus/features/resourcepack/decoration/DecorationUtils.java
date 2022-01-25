package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.RandomUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
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

	// It isn't pretty, but it works
	private static Set<Location> getConnectedHitboxes(HitboxMaze maze) {
		if (maze.getTries() > 50) {
			debug("returning, MAX TRIES");
			debug("===");
			return maze.getFound();
		}

		if (maze.getDirectionsLeft().isEmpty()) {
			if (maze.getBlock().getLocation().equals(maze.getOrigin().getLocation())) {
				debug("returning, origin == block");
				debug("===");
				Tasks.wait(maze.incWait(), () -> debugDot(maze.getBlock().getLocation(), Color.ORANGE));
				return maze.getFound();
			}

			maze.goBack();

			Location newLoc = maze.getBlock().getLocation();
			String locStr = StringUtils.getShortLocationString(newLoc);
			debug("No directions left, going back");
			debug("New Loc: " + locStr);
			debug("New Dirs: " + maze.getDirectionsLeft());
			debug("- - -");
			Tasks.wait(maze.incWait(), () -> debugDot(newLoc, Color.RED));


			maze.incrementTries();
			return getConnectedHitboxes(maze);
		}

		maze.setTries(0);

		debug("Dirs Left: " + maze.getDirectionsLeft());
		maze.nextDirection();

		Block previousBlock = maze.getBlock();
		maze.setBlock(previousBlock.getRelative(maze.getBlockFace()));

		Block currentBlock = maze.getBlock();
		Location currentLoc = currentBlock.getLocation().clone();
		String currentLocStr = StringUtils.getShortLocationString(currentBlock.getLocation());
		debug("Loc: " + currentLocStr);

		Material currentType = currentBlock.getType();
		debug("Type: " + currentType);

		double distance = maze.getOrigin().getLocation().distance(currentLoc);
		Set<Material> hitboxTypes = DecorationType.getHitboxTypes();
		if (maze.getTried().contains(currentLoc) || !hitboxTypes.contains(currentType) || distance > 6) {
			if (!hitboxTypes.contains(currentType))
				debug("  - Not a hitbox type");
			if (distance > maze.getRadius())
				debug("  - distance > " + maze.getRadius());
			if (maze.getTried().contains(currentLoc))
				debug("  - already tried " + currentLocStr);

			if (!maze.getTried().add(currentLoc))
				debug("  Adding to tried: " + currentLocStr);

			debug("- - -");
			maze.setBlock(previousBlock);
			return getConnectedHitboxes(maze);
		}

		maze.addToPath(previousBlock.getLocation(), maze.getDirectionsLeft());

		debug("Found: " + currentLocStr + "");
		maze.getFound().add(currentLoc);

		debug("Adding to tried: " + currentLocStr);

		maze.getTried().add(currentLoc);

		maze.setDirectionsLeft(new ArrayList<>(hitboxDirections));
		maze.getDirectionsLeft().remove(maze.getBlockFace().getOppositeFace());
		maze.addToPath(currentLoc, maze.getDirectionsLeft());


		debug("- - -");
		debug("");
		Tasks.wait(maze.incWait(), () -> debugDot(currentBlock.getLocation(), Color.BLACK));

		return getConnectedHitboxes(maze);
	}

	static ItemFrame findItemFrame(Set<Location> connectedHitboxes, Block clicked) {
		Location clickedLoc = clicked.getLocation();

		Map<Location, HitboxData> dataMap = new HashMap<>();
		for (Location location : connectedHitboxes) {
			Block block = location.getBlock();
			ItemFrame itemFrame = block.getLocation().toCenterLocation().getNearbyEntitiesByType(ItemFrame.class, 0.5).stream().findFirst().orElse(null);
			if (itemFrame == null)
				continue;

			ItemStack itemStack = itemFrame.getItem();
			if (isNullOrAir(itemStack))
				continue;

			DecorationType type = DecorationType.of(itemStack);
			if (type == null)
				continue;

			if (dataMap.containsKey(location))
				continue;

			HitboxData hitboxData = new HitboxData.HitboxDataBuilder()
				.itemFrame(itemFrame)
				.block(block)
				.decorationType(type)
				.build();

			dataMap.put(block.getLocation(), hitboxData);
		}

		for (HitboxData hitboxData : dataMap.values()) {
			Decoration decoration = hitboxData.getDecorationType().getDecoration();
			List<Hitbox> hitboxes = Hitbox.rotateHitboxes(decoration, hitboxData.getItemFrame());

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

				debug(StringUtils.getShortLocationString(_block.getLocation()) + " == "
					+ StringUtils.getShortLocationString(clickedLoc));

				if (LocationUtils.isFuzzyEqual(_block.getLocation(), clickedLoc)) {
					debug("found correct decoration");
					debugDot(hitboxData.getLocation(), Color.LIME);
					return hitboxData.getItemFrame();
				}
			}
		}

		return null;
	}

	@Nullable
	static ItemFrame getItemFrame(Block clicked) {
		int radius = 4;

		if (isNullOrAir(clicked))
			return null;

		Location location = clicked.getLocation().toCenterLocation();
		if (location.getNearbyEntitiesByType(ItemFrame.class, radius).size() == 0)
			return null;

		Set<Material> hitboxTypes = DecorationType.getHitboxTypes();
		if (!hitboxTypes.contains(clicked.getType()))
			return null;

		// Single
		ItemFrame itemFrame = location.getNearbyEntitiesByType(ItemFrame.class, 0.5).stream().findFirst().orElse(null);
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
		Set<Location> connectedHitboxes = getConnectedHitboxes(new HitboxMaze(clicked, radius));
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
			DotEffect.debug(Dev.WAKKA.getPlayer(), location.clone().toCenterLocation(), color, TickTime.SECOND.x(1));
	}

	@Data
	@AllArgsConstructor
	static class HitboxMaze {
		Block origin;
		int radius;
		Block block;
		BlockFace blockFace;
		List<BlockFace> directionsLeft = new ArrayList<>(hitboxDirections);
		Set<Location> found = new HashSet<>();
		LinkedList<Location> tempPath = new LinkedList<>();
		LinkedList<Location> resultPath = new LinkedList<>();
		HashMap<Location, List<BlockFace>> pathDirs = new HashMap<>();
		Set<Location> tried = new HashSet<>();
		int tries = 0;
		int wait = 0;


		public HitboxMaze(Block clicked, int radius) {
			this.origin = clicked;
			this.radius = radius;
			this.block = this.origin;
			addToPath(this.origin.getLocation(), this.directionsLeft);

			this.tried.add(this.origin.getLocation());
			this.found.add(this.origin.getLocation());
		}

		public void incrementTries() {
			++this.tries;
		}

		public void addToPath(Location location, List<BlockFace> directionsLeft) {
			tempPath.add(location);
			resultPath.add(location);
			pathDirs.put(location, directionsLeft);
		}

		public void goBack() {
			Location back = tempPath.removeLast();
			setBlock(back.getBlock());
			setDirectionsLeft(pathDirs.get(back));
		}

		public int incWait() {
			this.wait += 2;
			return this.wait;
		}

		public void nextDirection() {
			this.setBlockFace(RandomUtils.randomElement(this.getDirectionsLeft()));
			this.getDirectionsLeft().remove(this.getBlockFace());
			debug("Removing Dir: " + this.getBlockFace());
		}
	}

	@Getter
	private static final List<BlockFace> directions = List.of(
		BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST,
		BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST);

	public static BlockFace rotateClockwise(BlockFace blockFace) {
		int size = directions.size() - 1;
		int index = (directions.indexOf(blockFace) + 1);

		if (index > size)
			index = 0;

		return directions.get(index);
	}
}
