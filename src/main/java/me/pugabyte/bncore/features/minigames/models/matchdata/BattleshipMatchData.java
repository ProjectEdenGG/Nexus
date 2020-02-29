package me.pugabyte.bncore.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.mechanics.Battleship;
import me.pugabyte.bncore.features.minigames.mechanics.Battleship.ShipType;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData.Grid.Coordinate;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.minigames.mechanics.Battleship.LETTERS;

@Data
@MatchDataFor(Battleship.class)
public class BattleshipMatchData extends MatchData {
	private boolean placingKits = true;
	private Map<Team, Grid> grids = new HashMap<>();
	private Map<Team, Map<ShipType, Ship>> ships = new HashMap<>();

	public BattleshipMatchData(Match match) {
		super(match);
		for (Team team : match.getArena().getTeams()) {
			grids.put(team, new Grid(match.getArena(), team));

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
		Location origin;
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
		private Arena arena;
		private List<Coordinate> coordinates = new ArrayList<>();

		public Grid(Arena arena, Team team) {
			this.arena = arena;
			this.team = team;
			this.otherTeam = arena.getTeams().stream().filter(_team -> _team != this.team).findFirst().get();

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

		public Coordinate getCoordinate(Location location) {
			Region region = arena.getRegion("a0_ships_" + team.getName());
			Location center = WEUtils.toLocation(region.getCenter());
			int over = (int) Math.round(Math.abs(location.getX() - center.getX()) / 4);
			String letter = LETTERS.substring(over, over + 1);
			int number = (int) Math.round(Math.abs(location.getZ() - center.getZ()) / 4);
			return getCoordinate(letter, number);
		}

		public Coordinate getCoordinate(String letter, int number) {
			return coordinates.stream()
					.filter(coordinate -> coordinate.getLetter().equalsIgnoreCase(letter))
					.filter(coordinate -> coordinate.getNumber() == number)
					.findFirst()
					.orElseThrow(() -> new InvalidInputException("Not a valid coordinate"));
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
				// TODO
				return null;
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
			}

		}
	}

	public enum State {
		HIT(true, ChatColor.RED),
		MISS(true, ChatColor.WHITE),
		OCCUPIED(false, null),
		EMPTY(false, ChatColor.BLUE);

		@Getter
		private boolean shotAt;
		@Getter
		private ChatColor color;

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
