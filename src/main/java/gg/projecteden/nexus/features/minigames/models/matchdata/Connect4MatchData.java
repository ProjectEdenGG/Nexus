package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.mechanics.Connect4;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.matchdata.BattleshipMatchData.NotYourTurnException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@MatchDataFor(Connect4.class)
public class Connect4MatchData extends MatchData {
	private final Team startingTeam;
	private Team winningTeam;
	private final Board board = new Board();

	public Connect4MatchData(Match match) {
		super(match);
		this.startingTeam = RandomUtils.randomElement(match.getArena().getTeams());
	}

	public class Board {
		public static final int HEIGHT = 6;
		public static final int WIDTH = 7;
		private final InARowSolver solver = new InARowSolver(HEIGHT, WIDTH, 4);
		@Getter
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

		public int getEmptyRow(int column) {
			Integer finalRow = null;
			for (int row = (HEIGHT - 1); row >= 0; row--) {
				if (at(row, column).isEmpty()) {
					finalRow = row;
					break;
				}
			}

			if (finalRow == null)
				throw new InvalidInputException("That column is full");

			return finalRow;
		}

		public List<InARowPiece> getWinningPieces() {
			return solver.getWinningPieces(board);
		}

		public void place(Team team, int column) {
			if (!team.equals(getTurnTeam())) {
				Minigames.debug("[Connect4] not your turn");
				throw new NotYourTurnException();
			}

			int emptyRow = getEmptyRow(column);
			Minigames.debug("[Connect4] Row: " + emptyRow);

			InARowPiece piece;
			try {
				piece = at(emptyRow, column);
				piece.setTeam(team);
			} catch (ArrayIndexOutOfBoundsException ex) {
				throw new InvalidInputException("That column is full");
			}

			Material concretePowder = team.getColorType().getConcretePowder();
			BlockData blockData = Bukkit.createBlockData(concretePowder);
			World world = arena.getWorld();

			List<Location> spawnLocations = new ArrayList<>();
			arena.worldedit()
				.getBlocks(arena.getRegion("place_" + column))
				.forEach(block -> {
					Location location = block.getLocation().toCenterLocation();
					spawnLocations.add(location);

					int y = world.getHighestBlockAt(location.getBlockX(), location.getBlockZ()).getLocation().getBlockY();
					int yDiff = location.getBlockY() - (y - 1);
					piece.getLocations().add(new Location(world, location.getX(), yDiff, location.getZ()));
				});

			for (Location location : spawnLocations) {
				FallingBlock fallingBlock = world.spawnFallingBlock(location, blockData);
				fallingBlock.setDropItem(false);
				fallingBlock.setInvulnerable(true);

				Minigames.debug("[Connect4] Spawning falling block at: " + location + " in column " + column);
			}

			Minigames.debug("[Connect4] Placed, checking win");

			if (solver.checkWin(board)) {
				winningTeam = team;
				match.broadcast(team.getColoredName() + "&3 Team has connected 4!");
				match.scored(team);
			}
		}
	}

	public void
	end() {
		isEnding = true;

		WorldEditUtils worldedit = arena.worldedit();
		Region regionFloor = arena.getRegion("reset_floor");

		// TODO: Array needs to match ingame board
		Material teamMaterial = winningTeam.getColorType().getConcretePowder();
		AtomicInteger wait = new AtomicInteger();
		for (int i = 0; i < 4; i++) {
			match.getTasks().wait(wait.getAndAdd(10), () -> {
				for (InARowPiece piece : board.getWinningPieces()) {
					for (Location location : piece.getLocations()) {
						location.getBlock().setType(Material.LIME_CONCRETE);
					}
				}
			});

			match.getTasks().wait(wait.getAndAdd(10), () -> {
				for (InARowPiece piece : board.getWinningPieces()) {
					for (Location location : piece.getLocations()) {
						location.getBlock().setType(teamMaterial);
					}
				}
			});
		}

		wait.getAndAdd(5);
		wait.getAndAdd((int) TickTime.SECOND.x(4));

		match.getTasks().wait(wait.get(), () -> {
			worldedit.getBlocks(regionFloor).forEach(block -> block.setType(Material.AIR));

			match.getTasks().wait(TickTime.SECOND.x(5), () ->
				worldedit.getBlocks(regionFloor).forEach(block -> block.setType(Material.YELLOW_WOOL)));
		});
	}

	//

	@Data
	public static class InARowPiece {
		private Team team;
		private List<Location> locations = new ArrayList<>();

		public InARowPiece(Team team) {
			this.team = team;
		}

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
			Team team = board[row][column].getTeam();
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
