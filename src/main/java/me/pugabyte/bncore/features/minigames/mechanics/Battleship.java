package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.commands.BattleshipCommand;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Match.MatchTasks.MatchTaskType;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.annotations.Scoreboard;
import me.pugabyte.bncore.features.minigames.models.arenas.BattleshipArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchBeginEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData.Grid;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData.Ship;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData.ShipType;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import me.pugabyte.bncore.features.minigames.models.scoreboards.MinigameScoreboard.Type;
import me.pugabyte.bncore.utils.BlockUtils;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.CardinalDirection;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event.Result;
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
import java.util.function.Consumer;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.getShortLocationString;
import static me.pugabyte.bncore.utils.Utils.attempt;

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
	public static final List<String> COORDINATES = new ArrayList<String>() {{
		for (int number = 0; number < 10; number++)
			for (String letter : LETTERS.split(""))
				add(letter + number);
	}};
	protected static final List<Material> floorMaterials = Arrays.asList(Material.YELLOW_WOOL, Material.BLUE_CONCRETE, Material.BLACK_CONCRETE);

	private void debug(String message) {
		if (BattleshipCommand.isDebug())
			BNCore.log(StringUtils.stripColor(PREFIX + message));
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
		return new ItemStack(Material.OAK_BOAT);
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public boolean shuffleSpawnpoints() {
		return false;
	}

	@Override
	public boolean allowFly() {
		return true;
	}

	@Override
	public String getScoreboardTitle(Match match) {
		return "&6&lBattleship";
	}

	@Override
	public Map<String, Integer> getScoreboardLines(Match match, Team team) {
		BattleshipArena arena = match.getArena();
		BattleshipMatchData matchData = match.getMatchData();

		List<String> lines = new ArrayList<>();
		lines.add("&cTime: &e" + "11s");
		lines.add("&cChoose in: &e" + "17s");
		lines.add("&f");

		Team otherTeam = arena.getOtherTeam(team);

		lines.add("&6&l" + team.getName() + " Fleet &e(You)");
		lines.add("&0" + getProgressBar(matchData, team));
		lines.add("&6&l" + otherTeam.getName() + " Fleet");
		lines.add("&1" + getProgressBar(matchData, otherTeam));

		// TODO: History

		return new HashMap<String, Integer>() {{
			for (int i = lines.size(); i > 0; i--)
				put(lines.get(lines.size() - i), i);
		}};
	}

	public String getProgressBar(BattleshipMatchData matchData, Team team) {
		return StringUtils.progressBar(matchData.getGrid(team).getHealth(), ShipType.getCombinedHealth(), StringUtils.ProgressBarStyle.COUNT);
	}

	@Override
	public void begin(MatchBeginEvent event) {
		super.begin(event);
		start(event.getMatch());
	}

	public void start(Match match) {
		match.getTasks().cancel(MatchTaskType.BEGIN_DELAY);
		BattleshipMatchData matchData = match.getMatchData();
		if (!matchData.isPlacingKits()) return;

		for (Team team : matchData.getShips().keySet()) {
			matchData.getShips().get(team).values().stream()
					.filter(ship -> ship.getOrigin() == null)
					.forEach(ship -> {
						if (!attempt(100, () -> KitPlacer.of(match, team, ship.getType()).run()))
							BNCore.warn("Could not place " + camelCase(ship.getType().name()) + " on team " + team.getName() + " in " + match.getArena().getName());
					});
		}

		matchData.setPlacingKits(false);

		matchData.getShips().forEach((team, ships) -> {
			for (ShipType shipType : ShipType.values())
				pasteShip(shipType, ships.get(shipType).getOrigin());
		});

		match.getMinigamers().forEach(minigamer -> {
			minigamer.getPlayer().getInventory().clear();
			minigamer.teleport(minigamer.getTeam().getSpawnpoints().get(1));
		});
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
		if (event.getBlockAgainst().getType() != Material.YELLOW_WOOL) return;

		Location floor = event.getBlockAgainst().getLocation();
		if (!minigamer.getMatch().getArena().isInRegion(floor, "floor")) return;

		BattleshipMatchData matchData = minigamer.getMatch().getMatchData();
		if (!matchData.isPlacingKits()) return;

		if (KitPlacer.of(minigamer, shipType, floor.add(0, 3, 0), BlockFace.NORTH).run())
			minigamer.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
	}

	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);
		if (event.useInteractedBlock() == Result.DENY) return;
		if (event.getClickedBlock() == null) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		BattleshipMatchData matchData = minigamer.getMatch().getMatchData();
		Team team = minigamer.getTeam();
		Block start = event.getClickedBlock();
		ShipType shipType = ShipType.get(start);
		if (shipType == null) return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			BlockFace direction = getKitDirection(start.getLocation());

			if (direction != null)
				KitPlacer.of(minigamer, shipType, start.getLocation(), getNextDirection(direction)).run();
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
		for (Block block : BlockUtils.getBlocksInRadius(location, 1))
			if (Tag.WOOL.isTagged(block.getType())) {
				block.setType(Material.AIR);
				deleteKit(block.getLocation());
			}
	}

	private static class KitPlacer {
		private final Match match;
		private final BattleshipMatchData matchData;
		private final Battleship mechanic;
		private final Minigamer minigamer;
		private final Team team;
		private final Grid grid;
		private final ShipType shipType;
		private final Ship ship;
		private final Location location;
		private BlockFace direction;
		private int attempts;

		public KitPlacer(Match match, Minigamer minigamer, Team team, ShipType shipType, Location location, BlockFace direction) {
			this.match = match;
			this.matchData = match.getMatchData();
			this.mechanic = match.getArena().getMechanic();
			this.minigamer = minigamer;
			this.team = team;
			this.grid = matchData.getGrid(team);
			this.shipType = shipType;
			this.ship = matchData.getShip(team, shipType);
			this.location = location;
			this.direction = direction;
		}

		public static KitPlacer of(Match match, Team team, ShipType shipType) {
			BattleshipMatchData matchData = match.getMatchData();
			Grid grid = matchData.getGrid(team);
			Location location = grid.getRandomCoordinate().getKitLocation();
			return of(match, null, team, shipType, location, CardinalDirection.random().toBlockFace());
		}

		public static KitPlacer of(Minigamer minigamer, ShipType shipType, Location location, BlockFace direction) {
			return of(minigamer.getMatch(), minigamer, minigamer.getTeam(), shipType, location, direction);
		}

		public static KitPlacer of(Match match, Minigamer minigamer, Team team, ShipType shipType, Location location, BlockFace direction) {
			return new KitPlacer(match, minigamer, team, shipType, location, direction);
		}

		private boolean run() {
			Consumer<String> send = message -> { if (minigamer != null) minigamer.send(message); };

			if (attempts >= 4) {
				if (location.getBlock().getType() == shipType.getItem().getType())
					send.accept(PREFIX + "Your &e" + shipType + " &3could not be rotated");
				else
					send.accept(PREFIX + "Your &e" + shipType + " &3doesnt fit there");
				return false;
			}

			++attempts;

			if (!kitFits(location, direction, shipType.getKitLength())) {
				this.direction = mechanic.getNextDirection(direction);
				return run();
			}

			if (location.getBlock().getType() == shipType.getItem().getType()) {
				mechanic.deleteKit(location);
				send.accept(PREFIX + "Rotated &e" + shipType);
			} else {
				if (location.getBlock().getType() != Material.AIR) {
					send.accept(PREFIX + "Your &e" + shipType + " &3doesnt fit there");
					return false;
				}

				send.accept(PREFIX + "Placed &e" + shipType);
			}

			grid.vacate(shipType);

			location.getBlock().setType(shipType.getItem().getType());
			grid.getCoordinate(location).occupy(ship);
			ship.setOrigin(location);

			Block index = location.getBlock();
			for (int i = 0; i < shipType.getKitLength(); i++) {
				index = index.getRelative(direction);
				index.setType(Material.WHITE_WOOL);
				grid.getCoordinate(index.getLocation()).occupy(ship);
			}

			return true;
		}

		private boolean kitFits(Location location, BlockFace direction, int kitLength) {
			Block index = location.getBlock();
			for (int i = 0; i < kitLength; i++) {
				index = index.getRelative(direction);
				if (index.getType() != Material.AIR)
					return false;

				Material floorType = index.getLocation().add(0, -3, 0).getBlock().getType();
				if (!floorMaterials.contains(floorType))
					return false;
			}

			return true;
		}
	}

	private BlockFace getNextDirection(BlockFace direction) {
		if (direction == null)
			return BlockFace.NORTH;

		return CardinalDirection.of(direction).turnRight().toBlockFace();
	}

	private void pasteShip(ShipType shipType, Location location) {
		BlockFace direction = getKitDirection(location).getOppositeFace();
		deleteKit(location);
		pasteShip(shipType, location, CardinalDirection.of(direction));
	}

	public void pasteShip(ShipType shipType, Location location, CardinalDirection direction) {
		String schematic = shipType.getFileName();
		debug("Pasting schematic " + schematic + " at " + getShortLocationString(location) + " with rotation " + direction.getRotation());
		new WorldEditUtils(location).paster()
				.file(schematic)
				.at(location)
				.transform(direction.getRotationTransform())
				.pasteAsync();
	}

	public BlockFace getKitDirection(Location location) {
		List<Block> blocks = BlockUtils.getBlocksInRadius(location, 1);
		BlockFace direction = null;
		for (Block block : blocks)
			if (block.getType() == Material.WHITE_WOOL)
				if (CardinalDirection.isCardinal(block.getFace(location.getBlock())))
					direction = block.getFace(location.getBlock()).getOppositeFace();

		debug("Kit direction: " + (direction == null ? "null" : direction.name().toLowerCase()));
		return direction;
	}

}
