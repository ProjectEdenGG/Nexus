package gg.projecteden.nexus.features.minigames.models.matchdata.shared;

import gg.projecteden.nexus.features.minigames.models.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Data
public class InARowBoard {
	protected final int height;
	protected final int width;
	protected final int required;
	protected final InARowSolver solver;
	protected final InARowPiece[][] board;

	public InARowBoard(int height, int width, int required) {
		this.height = height;
		this.width = width;
		this.required = required;
		this.solver = new InARowSolver();
		this.board = new InARowPiece[height][width];

		for (int row = 0; row < height; row++)
			for (int column = 0; column < width; column++)
				this.board[row][column] = new InARowPiece(column, row);
	}

	public InARowPiece getPiece(int row, int col) {
		return board[row][col];
	}

	public List<InARowPiece> getWinningPieces() {
		return solver.getWinningPieces();
	}

	@Data
	public static class InARowPiece {
		private final int x;
		private final int y;
		private Team team;
		private List<Location> locations = new ArrayList<>();

		public boolean isEmpty() {
			return team == null;
		}

	}

	public class InARowSolver {
		private int winningRow;
		private int winningColumn;
		private CheckDirection winningDirection;

		public boolean checkFull() {
			for (int row = 0; row < height; row++) {
				for (int column = 0; column < width; column++) {
					InARowPiece piece = board[row][column];
					if (piece.isEmpty())
						return false;
				}
			}

			return true;
		}

		public boolean checkWin() {
			for (int row = 0; row < height; row++) { // rows: bottom -> top
				for (int column = 0; column < width; column++) { // columns: left -> right
					if (board[row][column] == null || board[row][column].isEmpty()) {
						continue;
					}

					if (column + (required - 1) < width) { // Checks right
						if (this.check(row, column, CheckDirection.RIGHT))
							return true;
					}

					if (row + (required - 1) < height) { // Checks up
						if (this.check(row, column, CheckDirection.UP))
							return true;

						if (column + (required - 1) < width) { // Checks Diagonally Up and Right
							if (this.check(row, column, CheckDirection.DIAGONAL_RIGHT))
								return true;
						}

						if (column - (required - 1) >= 0) { // Checks Diagonally Up and Left
							if (this.check(row, column, CheckDirection.DIAGONAL_LEFT))
								return true;
						}
					}
				}
			}

			return false;
		}

		private boolean check(int row, int column, CheckDirection checkDirection) {
			Team team = board[row][column].getTeam();
			for (int i = 1; i < required; i++) {
				InARowPiece piece = board[row + (i * checkDirection.getRDelta())][column + (i * checkDirection.getCDelta())];
				if (piece == null || team != piece.getTeam())
					return false;
			}
			this.winningRow = row;
			this.winningColumn = column;
			this.winningDirection = checkDirection;
			return true;
		}

		public List<InARowPiece> getWinningPieces() {
			List<InARowPiece> winningPieces = new ArrayList<>();
			for (int i = 0; i < required; i++)
				winningPieces.add(board[this.winningRow + (i * this.winningDirection.getRDelta())][this.winningColumn + (i * this.winningDirection.getCDelta())]);
			return winningPieces;
		}

		@Getter
		@AllArgsConstructor
		public enum CheckDirection {
			RIGHT(0, 1),
			UP(1, 0),
			DIAGONAL_RIGHT(1, 1),
			DIAGONAL_LEFT(1, -1);

			private final int rDelta;
			private final int cDelta;
		}

	}
}
