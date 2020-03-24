package me.pugabyte.bncore.features.minigames.mechanics;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.annotations.Scoreboard;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData.Grid;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData.Ship;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import me.pugabyte.bncore.features.minigames.models.scoreboards.MinigameScoreboard.Type;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
	Regions:
		team_<team>
		(a0|b1)_(ships|pegs)_<team>
		ships
		config
		floor
		grid
 */

@Regenerating("board")
@Scoreboard(sidebarType = Type.TEAM)
public class Battleship extends BalancedTeamMechanic {
	private static final String PREFIX = StringUtils.getPrefix("Battleship");
	public static final String LETTERS = "ABCDEFGHIJ";

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

	@Override
	public String getScoreboardTitle(Match match) {
		return "&6&lBattleship";
	}

	@Override
	public Map<String, Integer> getScoreboardLines(Match match, Team team) {
		BattleshipMatchData matchData = match.getMatchData();
		List<String> lines = new ArrayList<>();
		lines.add("&cFleet: " + team.getColoredName());
		lines.add("&cTime: &e" + "11s");
		lines.add("&cChoose in: &e" + "17s");
		lines.add("&f");

		Team team1 = match.getArena().getTeams().get(0);
		Team team2 = match.getArena().getTeams().get(1);

		lines.add("&6&l" + team1.getName() + " Fleet");
		lines.add("&0" + getProgressBar(matchData, team1));
		lines.add("&6&l" + team2.getName() + " Fleet");
		lines.add("&0" + getProgressBar(matchData, team2));

		// TODO: History

		return new HashMap<String, Integer>() {{
			for (int i = lines.size(); i > 0; i--)
				put(lines.get(lines.size() - i), i);
		}};
	}

	public String getProgressBar(BattleshipMatchData matchData, Team team1) {
		return StringUtils.progressBar(matchData.getGrid(team1).getHealth(), ShipType.getCombinedHealth(), StringUtils.ProgressBarStyle.COUNT);
	}

	private void start(Match match) {
		BattleshipMatchData matchData = match.getMatchData();
		if (!matchData.isPlacingKits()) return;

		for (Team team : matchData.getShips().keySet()) {
			long count = matchData.getShips().get(team).values().stream()
					.filter(ship -> ship.getOrigin() == null)
					.count();

			if (count > 0)
				match.broadcast("Cannot start yet, setup incomplete");
		}

		matchData.setPlacingKits(false);

		matchData.getShips().forEach((team, ships) -> {
			for (ShipType shipType : ShipType.values())
				pasteShip(shipType, ships.get(shipType).getOrigin());
		});
	}

	private Team getTeam(Arena arena, Location location) {
		for (ProtectedRegion region : WGUtils.getRegionsAt(location))
			if (arena.ownsRegion(region.getId(), "team"))
				for (Team team : arena.getTeams())
					if (region.getId().split("_")[3].equalsIgnoreCase(team.getName().replaceAll(" ", "").toLowerCase()))
						return team;
		return null;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		event.setCancelled(true);

		ShipType shipType = ShipType.get(event.getBlockPlaced());
		if (shipType == null) return;
		if (event.getBlockAgainst().getType() != Material./*1.13 YELLOW_*/WOOL) return;

		Location floor = event.getBlockAgainst().getLocation();
		if (!minigamer.getMatch().getArena().isInRegion(floor, "floor")) return;

		// TODO: if (!matchData.isPlacingKits()) return;

		if (placeKit(minigamer, shipType, floor.add(0, 3, 0), BlockFace.NORTH))
			minigamer.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
	}

	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);
		if (event.isCancelled()) return;
		if (event.getHand() != EquipmentSlot.HAND) return;


		BattleshipMatchData matchData = minigamer.getMatch().getMatchData();
		Team team = minigamer.getTeam();
		Block start = event.getClickedBlock();
		ShipType shipType = ShipType.get(start);
		if (shipType == null) return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			BlockFace direction = getKitDirection(start.getLocation());

			if (direction != null)
				placeKit(minigamer, shipType, start.getLocation(), getNextDirection(direction));
		} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			event.setCancelled(true);
			minigamer.send(PREFIX + "Removed &e" + shipType);
			deleteKit(start.getLocation());
			matchData.getGrid(team).vacate(shipType);
			giveKitItem(minigamer, shipType);
		}
	}

	private void giveKitItem(Minigamer minigamer, ShipType shipType) {
		PlayerInventory inventory = minigamer.getPlayer().getInventory();
		if (inventory.getItemInMainHand().getType() == Material.AIR)
			inventory.setItemInMainHand(shipType.getItem());
		else
			Utils.giveItem(minigamer.getPlayer(), shipType.getItem());
	}

	private void deleteKit(Location location) {
		location.getBlock().setType(Material.AIR);
		for (Block block : Utils.getBlocksInRadius(location, 1))
			if (block.getType() == Material.WOOL) {
				block.setType(Material.AIR);
				deleteKit(block.getLocation());
			}
	}

	private boolean placeKit(Minigamer minigamer, ShipType shipType, Location location, BlockFace direction) {
		return placeKit(minigamer, shipType, location, direction, 0);
	}

	private boolean placeKit(Minigamer minigamer, ShipType shipType, Location location, BlockFace direction, int attempts) {
		if (attempts >= 4) {
			if (location.getBlock().getType() == shipType.getItem().getType())
				minigamer.send(PREFIX + "Your &e" + shipType + " &3could not be rotated");
			else
				minigamer.send(PREFIX + "Your &e" + shipType + " &3doesnt fit there");
			return false;
		}

		++attempts;

		if (!kitFits(location, direction, shipType.getKitLength()))
			return placeKit(minigamer, shipType, location, getNextDirection(direction), attempts);

		if (location.getBlock().getType() == shipType.getItem().getType()) {
			deleteKit(location);
			minigamer.send(PREFIX + "Rotated &e" + shipType);
		} else {
			if (location.getBlock().getType() != Material.AIR) {
				minigamer.send(PREFIX + "Your &e" + shipType + " &3doesnt fit there");
				return false;
			}

			minigamer.send(PREFIX + "Placed &e" + shipType);
		}

		BattleshipMatchData matchData = minigamer.getMatch().getMatchData();
		Team team = minigamer.getTeam();
		Grid grid = matchData.getGrid(team);
		Ship ship = matchData.getShip(team, shipType);
		grid.vacate(shipType);

		location.getBlock().setType(shipType.getItem().getType());
		location.getBlock().setData((byte) shipType.getItem().getDurability());
		grid.getCoordinate(location).occupy(ship);
		ship.setOrigin(location);

		Block index = location.getBlock();
		for (int i = 0; i < shipType.getKitLength(); i++) {
			index = index.getRelative(direction);
			index.setType(Material./*1.13 WHITE_*/WOOL);
			grid.getCoordinate(index.getLocation()).occupy(ship);
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

	private void pasteShip(ShipType shipType, Location location) {
		BlockFace direction = getKitDirection(location);
		deleteKit(location);
		WEUtils.paste("battleship/" + shipType.name().toLowerCase() + "/" + direction.name().toLowerCase(), location);
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

	public enum ShipType {
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

		ShipType(int length, ColorType color) {
			this.length = length;
			this.color = color;
			this.item = new ItemBuilder(Material.CONCRETE)
					.name(color.getChatColor() + toString() + " &8| &7Size: &e" + length)
					.lore("&fPlace on the yellow wool to configure")
					.durability(color.getDurability())
					.build();
		}

		@Override
		public String toString() {
			return StringUtils.camelCase(name());
		}

		public int getKitLength() {
			return (length - 1) * 4;
		}

		public static ShipType get(Block block) {
			for (ShipType shipType : ShipType.values())
				if (block.getData() == shipType.getColor().getDurability())
					return shipType;

			return null;
		}

		public static int getCombinedHealth() {
			return Arrays.stream(values()).mapToInt(ShipType::getLength).sum();
		}

	}

}
