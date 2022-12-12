package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Distance.distance;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class DecorationUtils {
	@Getter
	public static final String prefix = StringUtils.getPrefix("Decoration");
	public static final Set<UUID> debuggers = new HashSet<>();
	public static final List<BlockFace> cardinalFaces = List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

	public static void error(Player player) {
		if (player == null)
			return;

		error(player, "&c&lHey! &7Sorry, but you can't use that here.");
	}

	public static void error(Player player, String error) {
		if (player == null)
			return;

		PlayerUtils.send(player, error);
	}

	public static void debug(Player player, String message) {
		if (player == null)
			return;

		if (debuggers.contains(player.getUniqueId()))
			PlayerUtils.send(player, message);
	}

	public static void debug(Player player, Runnable runnable) {
		if (player == null)
			return;

		if (debuggers.contains(player.getUniqueId()))
			runnable.run();
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

	@Nullable
	public static ClientSideItemFrame getClientsideItemFrame(Block clicked, int radius, Player debugger) {
		if (isNullOrAir(clicked))
			return null;

		Location location = clicked.getLocation().toCenterLocation();
		if (ClientSideConfig.getEntities(location, radius).size() == 0)
			return null;

		Set<Material> hitboxTypes = DecorationConfig.getHitboxTypes();
		if (!hitboxTypes.contains(clicked.getType()))
			return null;

		// Single
		ClientSideItemFrame entity = findItemframe(location);
		if (entity != null)
			return entity;

		// Multi
		HitboxMaze maze = new HitboxMaze(debugger, clicked, radius);
		return getClientsideConnectedHitboxes(maze);
	}

	@Nullable
	private static ClientSideItemFrame findItemframe(Location location) {
		for (var entity : ClientSideConfig.getEntities(location)) {
			if (entity.getType() == ClientSideEntityType.ITEM_FRAME)
				return (ClientSideItemFrame) entity;
		}
		return null;
	}

	@Nullable
	public static ItemFrame getItemFrame(Block clicked, int radius, BlockFace blockFaceOverride, Player debugger) {
		if (isNullOrAir(clicked))
			return null;

		Location location = clicked.getLocation().toCenterLocation();
		if (location.getNearbyEntitiesByType(ItemFrame.class, radius).size() == 0)
			return null;

		Set<Material> hitboxTypes = DecorationConfig.getHitboxTypes();
		if (!hitboxTypes.contains(clicked.getType()))
			return null;

		HitboxMaze maze = new HitboxMaze(debugger, clicked, radius);
		ItemFrame itemFrame = location.getNearbyEntitiesByType(ItemFrame.class, 0.5).stream().findFirst().orElse(null);

		if (itemFrame != null) {
			debug(debugger, "Single");
			return findItemFrame(maze, clicked, blockFaceOverride, debugger);
		} else {
			debug(debugger, "Maze Search");
			return getConnectedHitboxes(maze, blockFaceOverride, debugger);
		}
	}

	// It isn't pretty, but it works
	// TODO: optimize by checking nearest neighbors first
	private static @Nullable ClientSideItemFrame getClientsideConnectedHitboxes(HitboxMaze maze) {
		if (maze.getTries() > 10)
			return null;

		if (maze.getDirectionsLeft().isEmpty()) {
			Location newLoc = maze.getBlock().getLocation();

			if (newLoc.equals(maze.getOrigin().getLocation())) {
				maze.debugDot(newLoc, Color.ORANGE);
				return null;
			}

			maze.goBack();
			maze.debugDot(newLoc, Color.RED);

			maze.incTries();
			return getClientsideConnectedHitboxes(maze);
		}

		maze.nextDirection();
		maze.setTries(0);

		Block previousBlock = maze.getBlock();
		maze.setBlock(previousBlock.getRelative(maze.getBlockFace()));

		Block currentBlock = maze.getBlock();
		Location currentLoc = currentBlock.getLocation().clone();
		Material currentType = currentBlock.getType();

		Distance distance = distance(maze.getOrigin(), currentLoc);
		Set<Material> hitboxTypes = DecorationConfig.getHitboxTypes();
		if (maze.getTried().contains(currentLoc) || !hitboxTypes.contains(currentType) || distance.gt(6)) {
			maze.setBlock(previousBlock);
			return getClientsideConnectedHitboxes(maze);
		}

		maze.getTried().add(currentLoc);
		maze.addToPath(previousBlock.getLocation(), maze.getDirectionsLeft());

		// Is correct item frame?
		ClientSideItemFrame entity = findItemframe(currentLoc);
		if (entity != null)
			return entity;

		// Keep looking
		maze.resetDirections();
		maze.addToPath(currentLoc, maze.getDirectionsLeft());
		maze.debugDot(currentLoc, Color.BLACK);
		return getClientsideConnectedHitboxes(maze);
	}

	// It isn't pretty, but it works
	// TODO: optimize by checking nearest neighbors first
	private static @Nullable ItemFrame getConnectedHitboxes(HitboxMaze maze, BlockFace blockFaceOverride, Player debugger) {
		if (maze.getTries() > 10) {
			debug(debugger, "Maze Tries > 10");
			return null;
		}

		if (maze.getDirectionsLeft().isEmpty()) {
			Location newLoc = maze.getBlock().getLocation();

			if (newLoc.equals(maze.getOrigin().getLocation())) {
				maze.debugDot(newLoc, Color.ORANGE);
				debug(debugger, "Maze returned to origin");
				return null;
			}

			maze.goBack();
			maze.debugDot(newLoc, Color.RED);

			maze.incTries();
			return getConnectedHitboxes(maze, blockFaceOverride, debugger);
		}

		maze.nextDirection();
		maze.setTries(0);

		Block previousBlock = maze.getBlock();
		maze.setBlock(previousBlock.getRelative(maze.getBlockFace()));

		Block currentBlock = maze.getBlock();
		Location currentLoc = currentBlock.getLocation().clone();
		Material currentType = currentBlock.getType();

		Distance distance = distance(maze.getOrigin(), currentLoc);
		Set<Material> hitboxTypes = DecorationConfig.getHitboxTypes();
		if (maze.getTried().contains(currentLoc) || !hitboxTypes.contains(currentType) || distance.gt(6)) {
			maze.setBlock(previousBlock);
			return getConnectedHitboxes(maze, blockFaceOverride, debugger);
		}

		maze.getTried().add(currentLoc);
		maze.addToPath(previousBlock.getLocation(), maze.getDirectionsLeft());

		// Is correct item frame?
		ItemFrame itemFrame = findItemFrame(maze, currentBlock, blockFaceOverride, debugger);
		if (itemFrame != null) {
			debug(debugger, "Maze found item frame");
			return itemFrame;
		}

		// Keep looking
		maze.resetDirections();
		maze.addToPath(currentLoc, maze.getDirectionsLeft());
		maze.debugDot(currentLoc, Color.BLACK);
		return getConnectedHitboxes(maze, blockFaceOverride, debugger);
	}

	private static @Nullable ItemFrame findItemFrame(@NonNull HitboxMaze maze, @NonNull Block current, BlockFace blockFaceOverride, Player debugger) {
		ItemFrame itemFrame = current.getLocation().toCenterLocation().getNearbyEntitiesByType(ItemFrame.class, 0.5).stream().findFirst().orElse(null);
		if (itemFrame == null) {
			debug(debugger, "- no item frames found nearby");
			return null;
		}

		ItemStack itemStack = itemFrame.getItem();
		if (isNullOrAir(itemStack)) {
			debug(debugger, "- item frame is empty");
			return null;
		}

		DecorationConfig config = DecorationConfig.of(itemStack);
		if (config == null) {
			debug(debugger, "- item frame does not have decoration");
			return null;
		}

		BlockFace blockFace = ItemFrameRotation.of(itemFrame).getBlockFace();
		debug(debugger, "Hitbox BlockFace: " + blockFace);
		if (config.isMultiBlockWallThing() && blockFaceOverride != null) {
			blockFace = blockFaceOverride;
			debug(debugger, "BlockFace Override 1: " + blockFace);
		}

		List<Hitbox> hitboxes = Hitbox.rotateHitboxes(config, blockFace);

		Location blockLoc = current.getLocation();
		Location originLoc = maze.getOrigin().getLocation();

		for (Hitbox hitbox : hitboxes) {
			Block _block = hitbox.getOffsetBlock(blockLoc);
			Location _blockLoc = _block.getLocation();

			maze.debugDot(_blockLoc, Color.TEAL);

			if (LocationUtils.isFuzzyEqual(_blockLoc, originLoc)) {
				debug(debugger, "origin is in hitbox, returning item frame");
				return itemFrame;
			}
		}

		debug(debugger, "- origin isn't in hitbox");
		return null;
	}
}
