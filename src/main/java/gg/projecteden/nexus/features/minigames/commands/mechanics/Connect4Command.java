package gg.projecteden.nexus.features.minigames.commands.mechanics;

import gg.projecteden.nexus.features.minigames.mechanics.Connect4;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.arenas.Connect4Arena;
import gg.projecteden.nexus.features.minigames.models.matchdata.Connect4MatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.Connect4MatchData.Connect4Board;
import gg.projecteden.nexus.features.minigames.models.matchdata.Connect4MatchData.Connect4Evaluator;
import gg.projecteden.nexus.features.minigames.models.matchdata.shared.InARowBoard.InARowPiece;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import net.md_5.bungee.api.ChatColor;

@HideFromWiki
@Permission(Group.ADMIN)
@Aliases("c4")
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Connect4Command extends CustomCommand {
	private final Minigamer minigamer;
	private Connect4 mechanic;

	private Match match;
	private Connect4Arena arena;
	private Connect4MatchData matchData;

	private Connect4Board board;
	private Team team;

	Connect4Command(CommandEvent event) {
		super(event);
		minigamer = Minigamer.of(player());

		if (minigamer.isIn(Connect4.class)) {
			match = minigamer.getMatch();
			arena = match.getArena();
			mechanic = match.getMechanic();
			matchData = match.getMatchData();
			board = matchData.getBoard();
			team = minigamer.getTeam();
		} else if (isCommandEvent())
			error("You must be playing Connect 4 to use this command");
	}

	@TabCompleteIgnore
	@Path("place <column>")
	void place(@Arg(min = 0, max = 6) int column) {
		board.tryPlace(team, column);
	}

	@Path("debug board")
	@Description("Print the in-memory copy of the board to chat")
	void debug_board() {
		for (int row = 0; row < board.getHeight(); row++) {
			String columns = "&3Row &e" + row + "&3: ";
			for (int column = 0; column < board.getWidth(); column++) {
				InARowPiece piece = board.getPiece(row, column);
				ChatColor color = ChatColor.WHITE;
				if (!piece.isEmpty())
					color = piece.getTeam().getChatColor();

				columns += color + "⬛";
			}
			send(columns);
		}
	}

	@Path("evaluate")
	void evaluate_board() {
		Connect4Evaluator.EvaluationResult result = Connect4Evaluator.evaluateBoard(board.getBoard(), arena.getTeams().getFirst(), arena.getTeams().getLast());
		send(result.toString());
	}
}
