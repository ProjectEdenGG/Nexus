package me.pugabyte.bncore.features.minigames.commands;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.mechanics.Battleship;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.arenas.BattleshipArena;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData.Grid;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData.Grid.Coordinate;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData.ShipType;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.CardinalDirection;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aliases("bs")
@Permission("group.staff")
public class BattleshipCommand extends CustomCommand {
	@Getter
	private static boolean debug;

	private Minigamer minigamer;
	private Battleship mechanic;

	private Match match;
	private BattleshipArena arena;
	private BattleshipMatchData matchData;
	private Grid grid;

	public BattleshipCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer()) {
			minigamer = PlayerManager.get(player());
			mechanic = (Battleship) MechanicType.BATTLESHIP.get();
			if (minigamer.isPlaying(Battleship.class)) {
				match = minigamer.getMatch();
				arena = minigamer.getMatch().getArena();
				matchData = minigamer.getMatch().getMatchData();
				grid = matchData.getGrid(minigamer.getTeam());
			}
		}
	}

	@Path("kit")
	void kit() {
		Arrays.asList(ShipType.values()).forEach(shipType -> Utils.giveItem(player(), shipType.getItem()));
	}

	@Path("debug")
	void debug() {
		debug = !debug;
		send(PREFIX + "Debug " + (debug ? "&aenabled" : "&cdisabled"));
	}

	@Path("getChatGrid")
	void getChatGrid() {
		grid.getChatGrid().forEach(this::send);
	}

	@Path("pasteShip <shipType> <direction>")
	void pasteShip(ShipType shipType, CardinalDirection direction) {
		mechanic.pasteShip(shipType, player().getLocation(), direction);
	}

	@Path("toKitLocation <coordinate>")
	void toKitLocation(Coordinate coordinate) {
		minigamer.teleport(Utils.getCenteredLocation(coordinate.getKitLocation()));
	}

	@Path("toPegLocation <coordinate>")
	void toPegLocation(Coordinate coordinate) {
		minigamer.teleport(Utils.getCenteredLocation(coordinate.getPegLocation()));
	}

	@Path("belay")
	void belay() {
		grid.belay();
	}

	private static final String[] aimMenuLines = {"", "^ ^ ^ ^ ^ ^", "Enter a", "coordinate (A0)"};

	@Path("aim [coordinate]")
	void aim(Coordinate coordinate) {
		if (coordinate != null)
			coordinate.aim();
		else
			BNCore.getSignMenuFactory()
					.lines(aimMenuLines)
					.prefix(PREFIX)
					.response(lines -> grid.getCoordinate(lines[0]).aim())
					.open(player());
	}

	@Path("fire random")
	void fireRandom() {
		grid.getRandomCoordinate().fire();
	}

	@Path("fire [coordinate]")
	void fire(Coordinate coordinate) {
		if (coordinate == null)
			coordinate = grid.getAiming();
		coordinate.fire();
	}

	@Path("start")
	void start() {
		mechanic.start(match);
	}

	@ConverterFor(Coordinate.class)
	Coordinate convertToCoordinate(String value, Team context) {
		if (context == null)
			context = minigamer.getTeam();

		return matchData.getGrid(context).getCoordinate(value);
	}

	@TabCompleterFor(Coordinate.class)
	List<String> tabCompleteCoordinate(String filter) {
		return Battleship.COORDINATES.stream()
				.filter(name -> name != null && name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

}
