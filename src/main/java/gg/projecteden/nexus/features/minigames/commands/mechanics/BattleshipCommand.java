package gg.projecteden.nexus.features.minigames.commands.mechanics;

import gg.projecteden.nexus.features.menus.api.SignMenuFactory;
import gg.projecteden.nexus.features.minigames.mechanics.Battleship;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.arenas.BattleshipArena;
import gg.projecteden.nexus.features.minigames.models.matchdata.BattleshipMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.BattleshipMatchData.Grid;
import gg.projecteden.nexus.features.minigames.models.matchdata.BattleshipMatchData.Grid.Coordinate;
import gg.projecteden.nexus.features.minigames.models.matchdata.BattleshipMatchData.ShipType;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.LocationUtils.CardinalDirection;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aliases("bs")
@HideFromWiki
public class BattleshipCommand extends CustomCommand {
	@Getter
	private static boolean debug;

	private Minigamer minigamer;
	private Battleship mechanic;

	private Match match;
	private BattleshipArena arena;
	private BattleshipMatchData matchData;
	private Grid grid;
	private Team team;
	private Team otherTeam;

	public BattleshipCommand(@NonNull CommandEvent event) {
		super(event);
		minigamer = Minigamer.of(player());
		if (minigamer.isIn(Battleship.class)) {
			mechanic = (Battleship) MechanicType.BATTLESHIP.get();
			match = minigamer.getMatch();
			arena = minigamer.getMatch().getArena();
			matchData = minigamer.getMatch().getMatchData();
			grid = matchData.getGrid(minigamer.getTeam());
			team = minigamer.getTeam();
			otherTeam = grid.getOtherTeam();
		} else if (isCommandEvent())
			error("You must be playing Battleship to use this command");
	}

	private static final String[] aimMenuLines = {"", SignMenuFactory.ARROWS, "Enter a", "coordinate (A0)"};

	@Path("aim [coordinate]")
	void aim(@Arg(permission = Group.MODERATOR) Coordinate coordinate) {
		if (coordinate != null)
			coordinate.aim();
		else
			grid.setAiming(true);
	}

	@Path("aim random")
	void fireRandom() {
		grid.getRandomCoordinate().aim();
	}

	@Path("belay")
	void belay() {
		grid.belay();
	}

	@Path("fire [coordinate]")
	void fire(@Arg(permission = Group.MODERATOR) Coordinate coordinate) {
		if (coordinate == null)
			coordinate = grid.getAimedAt();
		if (coordinate == null)
			error("You have not aimed your cannon yet");

		coordinate.fire();
		mechanic.nextTurn(match);
	}

	@Path("fire random")
	@Permission(Group.MODERATOR)
	void toolsFireRandom() {
		grid.getRandomCoordinate().fire();
		mechanic.nextTurn(match);
	}

	@Path("testWithKoda [start]")
	@Permission(Group.MODERATOR)
	void testWithKoda(@Arg("true") boolean start) {
		runCommand("mcmd mgm join alphavs ;; sudo koda mgm join alphavs ;; wait 20 ;; mgm start" + (start ? " ;; wait 20 ;; bs start" : ""));
	}

	@Path("start")
	@Permission(Group.MODERATOR)
	void start() {
		mechanic.begin(match);
	}

	@Path("kit")
	@Permission(Group.MODERATOR)
	void kit() {
		Arrays.asList(ShipType.values()).forEach(shipType -> PlayerUtils.giveItem(player(), shipType.getItem()));
	}

	@Path("hideShips")
	@Permission(Group.MODERATOR)
	void hideShips() {
		mechanic.hideShips(match, otherTeam);
	}

	@Path("debug")
	@Permission(Group.MODERATOR)
	void debug() {
		debug = !debug;
		send(PREFIX + "Debug " + (debug ? "&aenabled" : "&cdisabled"));
	}

	@Path("getChatGrid")
	@Permission(Group.MODERATOR)
	void getChatGrid() {
		grid.getChatGrid().forEach(this::send);
	}

	@Permission(Group.MODERATOR)
	@Path("pasteShip <shipType> <direction>")
	void pasteShip(ShipType shipType, CardinalDirection direction) {
		mechanic.pasteShip(shipType, location(), direction);
	}

	@Permission(Group.MODERATOR)
	@Path("toKitLocation <coordinate>")
	void toKitLocation(Coordinate coordinate) {
		minigamer.teleportAsync(LocationUtils.getCenteredLocation(coordinate.getKitLocation()));
	}

	@Permission(Group.MODERATOR)
	@Path("toPegLocation <coordinate>")
	void toPegLocation(Coordinate coordinate) {
		minigamer.teleportAsync(LocationUtils.getCenteredLocation(coordinate.getPegLocation()));
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
