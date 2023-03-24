package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.mechanics.Connect4;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.matchdata.BattleshipMatchData.NotYourTurnException;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;

import java.util.ArrayList;
import java.util.List;

@Data
@MatchDataFor(Connect4.class)
public class Connect4MatchData extends MatchData {
	private final Team startingTeam;
	private final Board board = new Board();

	public Connect4MatchData(Match match) {
		super(match);
		this.startingTeam = RandomUtils.randomElement(match.getArena().getTeams());
	}

	public class Board {
		private static final int HEIGHT = 6;
		private static final int WIDTH = 7;
		private final InARowSolver solver = new InARowSolver(HEIGHT, WIDTH, 4);
		private final InARowPiece[][] board;

		public Board() {
			this.board = new InARowPiece[HEIGHT][WIDTH];
			for (int row = 0; row < HEIGHT; row++) {
				for (int column = 0; column < WIDTH; column++) {
					this.board[row][column] = new InARowPiece(null);
				}
			}
		}

		public InARowPiece at(int row, int col) {
			return board[row][col];
		}

		public void place(Team team, int column) {
			if (!team.equals(getTurnTeam())) {  // TODO: it's always nobody's turn ??
				Dev.WAKKA.send("not your turn");
				throw new NotYourTurnException();
			}

			//
			int row = 0;
			while (at(row, column).isEmpty() && row <= 4) {
				row++;
				Dev.WAKKA.send("adding to row");
			}

			if (!at(row, column).isEmpty()) { // TODO: wtf is this
				row--;
				Dev.WAKKA.send("subtracting from row");
			}
			//
			Dev.WAKKA.send("Row: " + row);

			at(row, column).setTeam(team.getChatColor());

			Material concretePowder = team.getColorType().getConcretePowder();
			BlockData blockData = Bukkit.createBlockData(concretePowder);
			World world = arena.getWorld();
			arena.worldedit()
				.getBlocks(arena.getRegion("place_" + column))
				.forEach(block -> {
					FallingBlock fallingBlock = world.spawnFallingBlock(block.getLocation(), blockData);
					fallingBlock.setDropItem(false);
					fallingBlock.setInvulnerable(true);
					Dev.WAKKA.send("Spawning falling block at: " + block.getLocation() + " in column " + column);
				});

			Dev.WAKKA.send("Placed, checking win");

			if (solver.checkWin(board)) {
				match.broadcast("Winning Team: " + team.getName());
			}
		}
	}

	public void end() {
		isEnding = true;

		WorldEditUtils worldedit = arena.worldedit();
		Region regionFloor = arena.getRegion("reset_floor");
		Region regionTorch = arena.getRegion("reset_torch");
		Region regionLava = arena.getRegion("reset_lava");

		worldedit.getBlocks(regionFloor).forEach(block -> block.setType(Material.AIR));
		match.getTasks().wait(TickTime.SECOND.x(5), () -> {
			worldedit.getBlocks(regionTorch).forEach(block -> block.setType(Material.AIR));
			worldedit.getBlocks(regionLava).forEach(block -> block.setType(Material.LAVA));

			match.getTasks().wait(TickTime.SECOND.x(3), () -> {
				worldedit.getBlocks(regionLava).forEach(block -> block.setType(Material.BLACK_CONCRETE));
				worldedit.getBlocks(regionTorch).forEach(block -> block.setType(Material.TORCH));
				worldedit.getBlocks(regionFloor).forEach(block -> block.setType(Material.YELLOW_WOOL));
			});
		});
	}

	//

	@Data
	@AllArgsConstructor
	public static class InARowPiece {
		private ChatColor team;

		public boolean isEmpty() {
			return team == null;
		}

	}

	public static class InARowSolver {
		private final int HEIGHT;
		private final int WIDTH;
		private final int IN_A_ROW;
		private int winningRow;
		private int winningColumn;
		private CheckDirection winningDirection;

		public InARowSolver(int height, int width, int inARow) {
			this.HEIGHT = height;
			this.WIDTH = width;
			this.IN_A_ROW = inARow;
		}

		public boolean checkWin(InARowPiece[][] board) {
			for (int row = 0; row < this.HEIGHT; row++) { // rows: bottom -> top
				for (int column = 0; column < this.WIDTH; column++) { // columns: left -> right
					if (board[row][column] == null || board[row][column].isEmpty()) {
						continue;
					}

					if (column + (this.IN_A_ROW - 1) < this.WIDTH) { // Checks right
						if (this.check(board, row, column, CheckDirection.RIGHT)) {
							return true;
						}
					}
					if (row + (this.IN_A_ROW - 1) < this.HEIGHT) {
						if (this.check(board, row, column, CheckDirection.UP)) { // Checks Up
							return true;
						}
						if (column + (this.IN_A_ROW - 1) < this.WIDTH) { // Checks Diagonally Up and Right
							if (this.check(board, row, column, CheckDirection.DIAGONAL_RIGHT)) {
								return true;
							}
						}
						if (column - (this.IN_A_ROW - 1) >= 0) { // Checks Diagonally Up and Left
							if (this.check(board, row, column, CheckDirection.DIAGONAL_LEFT)) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}

		private boolean check(InARowPiece[][] board, int row, int column, CheckDirection checkDirection) {
			ChatColor team = board[row][column].getTeam();
			for (int i = 1; i < this.IN_A_ROW; i++) {
				InARowPiece piece = board[row + (i * checkDirection.getRDelta())][column + (i * checkDirection.getCDelta())];
				if (piece == null || team != piece.getTeam()) {
					return false;
				}
			}
			this.winningRow = row;
			this.winningColumn = column;
			this.winningDirection = checkDirection;
			return true;
		}

		public List<InARowPiece> getWinningPieces(InARowPiece[][] board) {
			List<InARowPiece> winningPieces = new ArrayList<>();
			for (int i = 0; i < this.IN_A_ROW; i++) {
				winningPieces.add(board[this.winningRow + (i * this.winningDirection.getRDelta())][this.winningColumn + (i * this.winningDirection.getCDelta())]);
			}
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
