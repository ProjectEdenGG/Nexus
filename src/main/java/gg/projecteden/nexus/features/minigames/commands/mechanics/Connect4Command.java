package gg.projecteden.nexus.features.minigames.commands.mechanics;

import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.mechanics.Connect4;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.arenas.Connect4Arena;
import gg.projecteden.nexus.features.minigames.models.matchdata.Connect4MatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.Connect4MatchData.Board;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@HideFromWiki
@Aliases("c4")
@Permission(Group.STAFF) // TODO - is this perm necessary?
public class Connect4Command extends CustomCommand {

	private Minigamer minigamer;
	private Connect4 mechanic;

	private Match match;
	private Connect4Arena arena;
	private Connect4MatchData matchData;

	private static Board board;
	private Team team;

	Connect4Command(CommandEvent event) {
		super(event);
		minigamer = Minigamer.of(player());

		if (minigamer.isIn(Connect4.class)) {
			mechanic = (Connect4) MechanicType.CONNECT4.get();
			match = minigamer.getMatch();
			arena = match.getArena();
			matchData = match.getMatchData();
			board = matchData.getBoard();
			team = minigamer.getTeam();
		} else if (isCommandEvent())
			error("You must be playing Connect 4 to use this command");
	}

	@Path("place <column>")
	@Permission(Group.ADMIN)
	void place(@Arg(min = 0, max = 7) int column) {
		if (!match.isStarted()) {
			Minigames.debug("[Connect4] match isn't started yet");
			error("The match has not started yet");
		}

		if (matchData.isEnding())
			return;

		Minigames.debug("[Connect4] Placing...");
		board.place(team, column);

		Minigames.debug("[Connect4] Next Turn");
		mechanic.nextTurn(match);
	}
}
