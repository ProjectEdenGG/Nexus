package me.pugabyte.bncore.features.minigames.mechanics;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;

/*
	Regions:
		team_<team>
		a0_(ships|pegs)_<team>
		ships
		config
		floor
		grid
 */

@Regenerating("board")
public class Battleship extends BalancedTeamMechanic {
	private static final String PREFIX = Utils.getPrefix("Battleship");

	private void debug(String message) {
		if (false)
			BNCore.log(ChatColor.stripColor(PREFIX + message));
	}

	@Override
	public String getName() {
		return "Battleship";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.BOAT);
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		event.setCancelled(true);

		Ship ship = Ship.get(event.getBlockPlaced());
		if (ship == null) return;
		if (event.getBlockAgainst().getType() != Material./*1.13 YELLOW_*/WOOL) return;

		Location floor = event.getBlockAgainst().getLocation();
		if (!minigamer.getMatch().getArena().isInRegion(floor, "floor")) return;

		// TODO: if (!matchData.isPlacingKits()) return;

		if (placeKit(minigamer, ship, floor.add(0, 3, 0), BlockFace.NORTH))
			minigamer.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
	}

	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);
		if (event.isCancelled()) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		Block start = event.getClickedBlock();
		Ship ship = Ship.get(start);
		if (ship == null) return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			BlockFace direction = getKitDirection(start.getLocation());

			if (direction != null)
				placeKit(minigamer, ship, start.getLocation(), getNextDirection(direction));
		} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			event.setCancelled(true);
			minigamer.send(PREFIX + "Removed &e" + ship);
			deleteKit(start.getLocation());
			giveKitItem(minigamer, ship);
		}
	}

	private void giveKitItem(Minigamer minigamer, Ship ship) {
		PlayerInventory inventory = minigamer.getPlayer().getInventory();
		if (inventory.getItemInMainHand().getType() == Material.AIR)
			inventory.setItemInMainHand(ship.getItem());
		else
			Utils.giveItem(minigamer.getPlayer(), ship.getItem());
	}

	private void deleteKit(Location location) {
		location.getBlock().setType(Material.AIR);
		for (Block block : Utils.getBlocksInRadius(location, 1))
			if (block.getType() == Material.WOOL) {
				block.setType(Material.AIR);
				deleteKit(block.getLocation());
			}
	}

	private boolean placeKit(Minigamer minigamer, Ship ship, Location location, BlockFace direction) {
		return placeKit(minigamer, ship, location, direction, 0);
	}

	private boolean placeKit(Minigamer minigamer, Ship ship, Location location, BlockFace direction, int attempts) {
		if (attempts >= 4) {
			if (location.getBlock().getType() == ship.getItem().getType())
				minigamer.send(PREFIX + "Your &e" + ship + " &3could not be rotated");
			else
				minigamer.send(PREFIX + "Your &e" + ship + " &3doesnt fit there");
			return false;
		}

		++attempts;

		if (!kitFits(location, direction, ship.getKitLength()))
			return placeKit(minigamer, ship, location, getNextDirection(direction), attempts);

		if (location.getBlock().getType() == ship.getItem().getType()) {
			deleteKit(location);
			minigamer.send(PREFIX + "Rotated &e" + ship);
		} else {
			if (location.getBlock().getType() != Material.AIR) {
				minigamer.send(PREFIX + "Your &e" + ship + " &3doesnt fit there");
				return false;
			}

			minigamer.send(PREFIX + "Placed &e" + ship);
		}

		location.getBlock().setType(ship.getItem().getType());
		location.getBlock().setData((byte) ship.getItem().getDurability());

		Block index = location.getBlock();
		for (int i = 0; i < ship.getKitLength(); i++) {
			index = index.getRelative(direction);
			index.setType(Material./*1.13 WHITE_*/WOOL);
		}

		return true;
	}

	private boolean kitFits(Location location, BlockFace direction, int kitLength) {
		debug("Direction: " + direction + ", Kit length: " + kitLength);
		Block index = location.getBlock();
		for (int i = 0; i < kitLength; i++) {
			index = index.getRelative(direction);
			if (index.getType() != Material.AIR) {
				debug("Kit doesnt fit, block is not air (" + index.getType() + ")");
				if (index.getType() == Material.WOOL) {
					index.setType(Material.REDSTONE_BLOCK);
					throw new BNException("Aborting");
				}

				return false;
			}

			// Hasnt gone off the board
			// 1.13 yellow wool, blue/black concrete
			List<Material> floorMaterials = Arrays.asList(Material.WOOL, Material.CONCRETE);
			Material floorType = index.getLocation().add(0, -3, 0).getBlock().getType();
			if (!floorMaterials.contains(floorType)) {
				debug("Kit doesnt fit, floor is not wool/concrete (" + floorType + ")");
				return false;
			}
		}

		return true;
	}

	private void pasteShip(Ship ship, Location location) {
		BlockFace direction = getKitDirection(location);
		deleteKit(location);
		WEUtils.paste("battleship/" + ship.name().toLowerCase() + "/" + direction.name().toLowerCase(), location);
	}

	public BlockFace getKitDirection(Location location) {
		List<Block> blocks = Utils.getBlocksInRadius(location, 1);
		BlockFace direction = null;
		for (Block block : blocks)
			if (block.getType() == Material./*1.13 WHITE_*/WOOL)
				if (isCardinal(block.getFace(location.getBlock())))
					direction = block.getFace(location.getBlock()).getOppositeFace();

		debug("Kit direction: " + (direction == null ? "null" : direction.name().toLowerCase()));
		return direction;
	}

	private BlockFace getNextDirection(BlockFace direction) {
		switch (direction) {
			case NORTH:
				return BlockFace.EAST;
			case EAST:
				return BlockFace.SOUTH;
			case SOUTH:
				return BlockFace.WEST;
			case WEST:
				return BlockFace.NORTH;
			default:
				return BlockFace.NORTH;
		}
	}

	private boolean isCardinal(BlockFace face) {
		return Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST).contains(face);
	}

	public enum Ship {
		CRUISER(2, ColorType.LIGHT_GREEN),
		SUBMARINE(3, ColorType.LIGHT_RED),
		DESTROYER(3, ColorType.PURPLE),
		BATTLESHIP(4, ColorType.ORANGE),
		CARRIER(5, ColorType.CYAN);

		@Getter
		private int length;
		@Getter
		private ColorType color;
		@Getter
		private ItemStack item;

		Ship(int length, ColorType color) {
			this.length = length;
			this.color = color;
			this.item = new ItemStackBuilder(Material.CONCRETE)
					.name(color.getChatColor() + toString())
					.lore("&fPlace on the yellow wool to configure")
					.durability(color.getDurability().shortValue())
					.build();
		}

		@Override
		public String toString() {
			return Utils.camelCase(name());
		}

		public int getKitLength() {
			return (length - 1) * 4;
		}

		public static Ship get(Block block) {
			for (Ship ship : Ship.values())
				if (block.getData() == ship.getColor().getDurability())
					return ship;

			return null;
		}
	}

}
