package me.pugabyte.bncore.features.minigames.commands;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.mechanics.Battleship;
import me.pugabyte.bncore.features.minigames.mechanics.Battleship.ShipType;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.BattleshipArena;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.CardinalDirection;

import java.util.Arrays;

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

	public BattleshipCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer()) {
			minigamer = PlayerManager.get(player());
			mechanic = (Battleship) MechanicType.BATTLESHIP.get();
			if (minigamer.isPlaying(Battleship.class)) {
				match = minigamer.getMatch();
				arena = minigamer.getMatch().getArena();
				matchData = minigamer.getMatch().getMatchData();
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
		matchData.getGrid(minigamer.getTeam()).getChatGrid().forEach(this::send);
	}

	@Path("pasteShip <shipType> <direction>")
	void pasteShip(ShipType shipType, CardinalDirection direction) {
		mechanic.pasteShip(shipType, player().getLocation(), direction);
	}

	@Path("start")
	void start() {
		mechanic.start(match);
	}

}
