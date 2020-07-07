package me.pugabyte.bncore.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.mechanics.Battleship;
import me.pugabyte.bncore.features.minigames.mechanics.Battleship.ShipType;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.bncore.features.minigames.models.arenas.BattleshipArena;
import me.pugabyte.bncore.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData.Grid.Coordinate;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.Axis;
import me.pugabyte.bncore.utils.Utils.CardinalDirection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.minigames.mechanics.Battleship.LETTERS;
import static me.pugabyte.bncore.utils.StringUtils.left;
import static me.pugabyte.bncore.utils.StringUtils.right;
import static me.pugabyte.bncore.utils.Utils.getDirection;
import static me.pugabyte.bncore.utils.Utils.isInt;

@Data
@MatchDataFor(Battleship.class)
public class BattleshipMatchData extends MatchData {
	private boolean placingKits = true;
	private Map<Team, Grid> grids = new HashMap<>();
	private Map<Team, Map<ShipType, Ship>> ships = new HashMap<>();

	public BattleshipMatchData(Match match) {
		super(match);
		for (Team team : match.getArena().getTeams()) {
			grids.put(team, new Grid(team));

			ships.put(team, new HashMap<ShipType, Ship>() {{
				for (ShipType shipType : ShipType.values())
					put(shipType, new Ship(team, shipType));
			}});
		}
	}

	public Grid getGrid(Team team) {
		return grids.get(team);
	}

	public Ship getShip(Team team, ShipType shipType) {
		return ships.get(team).get(shipType);
	}

	@Data
	public class Ship {
		@NonNull
		private Team team;
		@NonNull
		private ShipType type;
		private Location origin;
		private int health;
		private boolean alive = true;

		public Ship(@NonNull Team team, @NonNull ShipType type) {
			this.team = team;
			this.type = type;
			this.health = type.getLength();
		}

		public void fire() {
			--health;
			if (health == 0)
				alive = false;

		}

		public List<Coordinate> getCoordinates() {
			return getGrid(team).getCoordinates().stream()
					.filter(coordinate -> coordinate.getShip() != null)
					.filter(coordinate -> coordinate.getShip().getType() == type)
					.collect(Collectors.toList());
		}
	}

	@Data
	public class Grid {
		private Team team;
		private Team otherTeam;
		private BattleshipArena arena;
		private List<Coordinate> coordinates = new ArrayList<>();
		private Region a0_ships;
		private Region a0_pegs;
		private BlockFace letterDirection;
		private BlockFace numberDirection;

		public Grid(Team team) {
			this.arena = getMatch().getArena();
			this.team = team;
			this.otherTeam = arena.getOtherTeam(team);
			this.a0_ships = arena.getRegion("a0_ships_" + team.getName());
			this.a0_pegs = arena.getRegion("a0_pegs_" + team.getName());

			Location a0 = arena.getWEUtils().toLocation(a0_ships.getCenter());
			Utils.getBlocksInRadius(a0, 5).forEach(block -> {
				if (block.getType() != Material.YELLOW_WOOL) return;

				Axis axis = Axis.getAxis(a0, block.getLocation());
				if (axis == Axis.Z)
					letterDirection = getDirection(a0, block.getLocation());
				else if (axis == Axis.X)
					numberDirection = getDirection(a0, block.getLocation());
			});

			if (letterDirection == null)
				throw new MinigameException("Could not determine letter direction of " + arena.getName() + " - " + team.getName());

			if (numberDirection == null)
				throw new MinigameException("Could not determine number direction of " + arena.getName() + " - " + team.getName());

			for (String letter : LETTERS.split(""))
				for (int number = 0; number < 10; number++)
					coordinates.add(new Coordinate(letter, number));
		}

		public int getHealth() {
			return getShips().get(team).values().stream().mapToInt(Ship::getHealth).sum();
		}

		public List<String> getChatGrid() {
			return new ArrayList<String>(Collections.nCopies(10, "")) {{
				coordinates.forEach(coordinate -> {
					ChatColor color = coordinate.getState().getColor();
					if (coordinate.getState() == State.OCCUPIED)
						color = coordinate.getShip().getType().getColor().getChatColor();

					set(coordinate.getNumber(), get(coordinate.getNumber()) + color + "▇");
				});
			}};
		}

		public InvalidInputException invalidCoordinate() {
			return new InvalidInputException("Not a valid coordinate");
		}

		public Coordinate getCoordinate(String input) {
			if (input.length() != 2)
				throw invalidCoordinate();
			String letter = left(input, 1);
			String number = right(input, 1);
			if (!isInt(number))
				throw invalidCoordinate();

			return getCoordinate(letter, Integer.parseInt(number));
		}

		public Coordinate getCoordinate(Location location) {
			Region region = arena.getRegion("a0_ships_" + team.getName());
			Location a0 = WEUtils.toLocation(region.getCenter());
			int over = (int) Math.round(Math.abs(location.getX() - a0.getX()) / 4);
			String letter = LETTERS.substring(over, over + 1);
			int number = (int) Math.round(Math.abs(location.getZ() - a0.getZ()) / 4);
			return getCoordinate(letter, number);
		}

		public Coordinate getCoordinate(String letter, int number) {
			return coordinates.stream()
					.filter(coordinate -> coordinate.getLetter().equalsIgnoreCase(letter))
					.filter(coordinate -> coordinate.getNumber() == number)
					.findFirst()
					.orElseThrow(this::invalidCoordinate);
		}

		public void vacate(ShipType shipType) {
			getShip(team, shipType).getCoordinates().forEach(Coordinate::vacate);
		}

		@Data
		public class Coordinate {
			private String letter;
			private int number;

			private Ship ship;
			private State state = State.EMPTY;

			public Coordinate(String letter, int number) {
				this.letter = letter;
				this.number = number;
			}

			public Location getKitLocation() {
				Region region = arena.getRegion("a0_ships_" + team.getName());
				Location a0 = WEUtils.toLocation(region.getCenter());

				int over = LETTERS.indexOf(letter) * 4;
				int down = number * 4;

				return a0.getBlock()
						.getRelative(letterDirection, over)
						.getRelative(BlockFace.UP, 3)
						.getRelative(numberDirection, down)
						.getLocation();
			}

			public Location getPegLocation() {
				Region region = arena.getRegion("a0_pegs_" + team.getName());
				Location a0 = WEUtils.toLocation(region.getCenter());

				int over = LETTERS.indexOf(letter) * 4;
				int down = number * 4;

				return a0.getBlock()
						.getRelative(letterDirection, over)
						.getRelative(BlockFace.DOWN, down)
						.getLocation();
			}

			public void occupy(Ship ship) {
				this.ship = ship;
				this.state = State.OCCUPIED;
			}

			public void vacate() {
				this.ship = null;
				this.state = State.EMPTY;
			}

			public void fire() {
				if (state.isShotAt())
					throw new AlreadyShotAtException();

				if (state == State.OCCUPIED) {
					ship.fire();
					state = State.HIT;
				} else
					state = State.MISS;

				pastePeg(state == State.HIT ? Peg.HIT_THEM : Peg.MISS_THEM);
			}

			public void aim() {
				if (state.isShotAt())
					throw new AlreadyShotAtException();

				pastePeg(Peg.CONFIRMATION);
			}

			public void pastePeg(Peg peg) {
				arena.getWEUtils().paster()
						.file(peg.getFileName())
						.at(peg.getBoard().getLocation(this))
						.transform(CardinalDirection.of(numberDirection).getRotationTransform())
						.paste();

				if (peg.getOpposite() != null)
					getOppositeCoordinate().pastePeg(peg.getOpposite());
			}

			public Coordinate getOppositeCoordinate() {
				return getGrid(otherTeam).getCoordinate(letter, number);
			}
		}
	}

	public enum Peg {
		CONFIRMATION(PegBoard.VERTICAL),
		HIT_ME(PegBoard.HORIZONTAL),
		HIT_THEM(PegBoard.VERTICAL),
		MISS_ME(PegBoard.HORIZONTAL),
		MISS_THEM(PegBoard.VERTICAL);

		@Getter
		private final PegBoard board;
		private final String opposite;

		Peg(PegBoard board) {
			this.board = board;
			if (name().contains("_THEM"))
				this.opposite = name().replace("_THEM", "_ME");
			else
				this.opposite = null;
		}

		public Peg getOpposite() {
			if (opposite == null)
				return null;

			return Peg.valueOf(opposite);
		}

		public enum PegBoard {
			VERTICAL{
				@Override
				public Location getLocation(Coordinate coordinate) {
					return coordinate.getPegLocation();
				}
			},
			HORIZONTAL{
				@Override
				public Location getLocation(Coordinate coordinate) {
					return coordinate.getKitLocation();
				}
			};

			abstract public Location getLocation(Coordinate coordinate);
		}

		public String getFileName() {
			return "battleship/pegs/" + name().toLowerCase();
		}
	}

	public enum State {
		HIT(true, ChatColor.RED),
		MISS(true, ChatColor.WHITE),
		OCCUPIED(false, null),
		EMPTY(false, ChatColor.BLUE);

		@Getter
		private final boolean shotAt;
		@Getter
		private final ChatColor color;

		State(boolean shotAt, ChatColor color) {
			this.shotAt = shotAt;
			this.color = color;
		}
	}

	public static class AlreadyShotAtException extends BNException {
		public AlreadyShotAtException() {
			super("You already shot at that location");
		}
	}



}
