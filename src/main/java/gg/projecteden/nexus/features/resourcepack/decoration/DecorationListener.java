package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class DecorationListener implements Listener {
	private static final List<BlockFace> hitboxDirections = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);

	public DecorationListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(EntityDismountEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getDismounted() instanceof ArmorStand armorStand)) return;
		if (Seat.isSeat(armorStand)) {
			event.getDismounted().remove();
			player.teleport(player.getLocation().add(0, 0.5, 0));
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		EquipmentSlot slot = event.getHand();
		if (slot != EquipmentSlot.HAND)
			return;

		Block clicked = event.getClickedBlock();
		if (clicked == null)
			return;

		Player player = event.getPlayer();
		if (!new CooldownService().check(player, "decoration-interact", TickTime.TICK.x(10)))
			return;

		ItemStack tool = ItemUtils.getTool(player);

		if (isCancelled(event))
			return;

		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) rightClick(event, player, clicked, tool);
		else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) leftClick(event, player, clicked, tool);

	}

	private void leftClick(PlayerInteractEvent event, Player player, Block clicked, ItemStack tool) {
		// TODO: Remove
		if (!Dev.WAKKA.is(player)) return;
		//

		if (!canEdit(player, clicked.getLocation())) {
			event.setCancelled(true);
			error(player);
			return;
		}

		if (!isNullOrAir(tool))
			return;

		HitboxData hitboxData = getItemFrame(clicked);
		if (hitboxData == null)
			return;

		event.setCancelled(true);
		hitboxData.getDecorationType().getDecoration().destroy(player, hitboxData.getItemFrame());
	}

	private void rightClick(PlayerInteractEvent event, Player player, Block clicked, ItemStack tool) {
		if (isNullOrAir(tool)) {

			if (event.useItemInHand() == Result.DENY)
				return;

			HitboxData hitboxData = getItemFrame(clicked);
			if (hitboxData == null)
				return;

			// Interact
			event.setCancelled(true);
			hitboxData.getDecorationType().getDecoration().interact(player, hitboxData.getItemFrame(), clicked);
		} else {
			// TODO: Remove
			if (!Dev.WAKKA.is(player)) return;
			//

			if (!canEdit(player, clicked.getLocation())) {
				event.setCancelled(true);
				error(player);
				return;
			}

			// Place
			DecorationType type = DecorationType.of(tool);
			if (type == null) return;

			event.setCancelled(true);
			type.getDecoration().place(player, clicked, event.getBlockFace(), tool);
		}
	}

	@Nullable
	private DecorationListener.HitboxData getItemFrame(Block clicked) {
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
				DecorationType decorationType = DecorationType.of(itemStack);
				if (decorationType != null) {
					debug("Single");
					return new HitboxData(itemFrame, clicked, decorationType);
				}
			}
		}

		// Multi
		Set<Block> connectedHitboxes = getConnectedHitboxes(new HitboxMaze(clicked));
		debug("Connected Hitboxes: " + connectedHitboxes.size());

		return findItemFrame(connectedHitboxes, clicked);
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

	private static HitboxData findItemFrame(Set<Block> connectedHitboxes, Block clicked) {
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

			dataMap.put(block.getLocation(), new HitboxData(itemFrame, block, type));
		}

		for (HitboxData hitboxData : dataMap.values()) {
			Decoration decoration = hitboxData.getDecorationType().getDecoration();
			List<Hitbox> hitboxes = decoration.getHitboxes(hitboxData.getItemFrame());

			Block block = hitboxData.getBlock();

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
					return hitboxData;
				}
			}
		}

		return null;
	}

	public static boolean debug = false;

	private static void debug(String message) {
		if (debug)
			Dev.WAKKA.send(message);
	}

	private static void debugDot(Location location, Color color) {
		if (debug)
			DotEffect.debug(Dev.WAKKA.getPlayer(), location.clone().toCenterLocation(), color);
	}

	@Data
	@AllArgsConstructor
	private static class HitboxData {
		ItemFrame itemFrame;
		Block block;
		DecorationType decorationType;
	}

	@Data
	@AllArgsConstructor
	private static class HitboxMaze {
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

	private void error(Player player) {
		PlayerUtils.send(player, "&c&lHey! &7Sorry, but you can't use that here.");
	}

	private boolean canEdit(Player player, Location location) {
		if (!new WorldGuardUtils(player).getRegionsAt(location).isEmpty())
			return WorldGuardEditCommand.canWorldGuardEdit(player);

		return true;
	}

	private boolean isCancelled(PlayerInteractEvent event) {
		return event.useInteractedBlock() == Result.DENY || event.useInteractedBlock() == Result.DENY;
	}
}
