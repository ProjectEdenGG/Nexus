package gg.projecteden.nexus.features.minigames.commands.mechanics;

import gg.projecteden.nexus.features.minigames.mechanics.TicTacToe;
import gg.projecteden.nexus.features.minigames.mechanics.TicTacToe.TicTacToeCell;
import gg.projecteden.nexus.features.minigames.mechanics.TicTacToe.TicTacToeSign;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.arenas.TicTacToeArena;
import gg.projecteden.nexus.features.minigames.models.matchdata.TicTacToeMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.TicTacToeMatchData.TicTacToeBoard;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@HideFromWiki
@Permission(Group.ADMIN)
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class TicTacToeCommand extends CustomCommand {
	private final Minigamer minigamer;
	private TicTacToe mechanic;

	private Match match;
	private TicTacToeArena arena;
	private TicTacToeMatchData matchData;

	private TicTacToeBoard board;
	private Team team;

	TicTacToeCommand(CommandEvent event) {
		super(event);
		minigamer = Minigamer.of(player());

		if (minigamer.isIn(TicTacToe.class)) {
			match = minigamer.getMatch();
			arena = match.getArena();
			mechanic = arena.getMechanic();
			matchData = match.getMatchData();
			board = matchData.getBoard();
			team = minigamer.getTeam();
		} else if (isCommandEvent())
			error("You must be playing Connect 4 to use this command");
	}

	@TabCompleteIgnore
	@Path("place <cell> <sign>")
	void place(TicTacToeCell cell, TicTacToeSign sign) {
		board.tryPlace(team, cell, sign);
	}
}
