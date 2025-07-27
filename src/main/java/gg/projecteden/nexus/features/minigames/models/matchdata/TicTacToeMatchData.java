package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.mechanics.TicTacToe;
import gg.projecteden.nexus.features.minigames.mechanics.TicTacToe.TicTacToeCell;
import gg.projecteden.nexus.features.minigames.mechanics.TicTacToe.TicTacToeSign;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.matchdata.shared.InARowBoard;
import gg.projecteden.nexus.features.minigames.models.matchdata.shared.InARowBoard.InARowPiece;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import lombok.Data;
import org.bukkit.Material;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Data
@MatchDataFor(TicTacToe.class)
public class TicTacToeMatchData extends MatchData {
	private final TicTacToeBoard board = new TicTacToeBoard();

	public TicTacToeMatchData(Match match) {
		super(match);
	}

	public class TicTacToeBoard extends InARowBoard {
		public TicTacToeBoard() {
			super(3, 3, 3);
		}

		public void tryPlace(Team team, TicTacToeCell cell, TicTacToeSign sign) {
			Minigames.debug("[TicTacToe] Placing...");
			if (!match.isStarted())
				return;

			if (isEnding)
				return;

			if (!team.equals(turnTeam)) {
				team.broadcast(match, "&cWait for your turn");
				return;
			}

			var piece = cell.getPiece(this);

			if (piece.getTeam() != null) {
				team.broadcast(match, "&cThat cell is already occupied");
				return;
			}

			piece.setTeam(team);
			cell.paste(arena, sign);

			Minigames.debug("[TicTacToe] Placed, checking win");
			if (solver.checkWin()) {
				winnerTeam = team;
				match.broadcast(team.getAliveMinigamers(match).getFirst().getColoredName() + "&3 has connected 3 in a row!");
				match.scored(team);
				return;
			}

			Minigames.debug("[TicTacToe] Checking full");
			if (solver.checkFull()) {
				match.end();
				return;
			}

			Minigames.debug("[TicTacToe] Next Turn");
			match.<TeamMechanic>getMechanic().nextTurn(match);
		}
	}

	public long end() {
		isEnding = true;

		var winningPieces = board.getWinningPieces();
		var wait = new AtomicLong(TickTime.SECOND.x(3));

		for (int i = 0; i < 3; i++) {
			Consumer<Material> setBackdrop = material -> {
				for (InARowPiece piece : winningPieces)
					TicTacToeCell.from(piece).setBackdrop(arena, material);
			};

			match.getTasks().wait(wait.getAndAdd(TickTime.TICK.x(15)), () -> setBackdrop.accept(Material.LIME_CONCRETE));
			match.getTasks().wait(wait.getAndAdd(TickTime.TICK.x(15)), () -> setBackdrop.accept(Material.BLACK_CONCRETE));
		}

		return wait.get();
	}

}
