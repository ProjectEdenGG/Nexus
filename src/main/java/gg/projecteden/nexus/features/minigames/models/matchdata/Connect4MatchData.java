package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.mechanics.Connect4;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Tasks;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Data
@MatchDataFor(Connect4.class)
public class Connect4MatchData extends MatchData {
	private final Board board = new Board();
	protected boolean turnComplete;

	public Connect4MatchData(Match match) {
		super(match);
	}

	public class Board {
		public static final int HEIGHT = 6;
		public static final int WIDTH = 7;
		private final InARowSolver solver = new InARowSolver(HEIGHT, WIDTH, 4);
		@Getter
		private final InARowPiece[][] board;

		public Board() {
			this.board = new InARowPiece[HEIGHT][WIDTH];
			for (int row = 0; row < HEIGHT; row++)
				for (int column = 0; column < WIDTH; column++)
					this.board[row][column] = new InARowPiece(column, row);
		}

		public InARowPiece getPiece(int row, int col) {
			return board[row][col];
		}

		public int getEmptyRow(int column) {
			Integer finalRow = null;
			for (int row = (HEIGHT - 1); row >= 0; row--)
				if (getPiece(row, column).isEmpty()) {
					finalRow = row;
					break;
				}

			if (finalRow == null)
				throw new InvalidInputException("That column is full");

			return finalRow;
		}

		public List<InARowPiece> getWinningPieces() {
			return solver.getWinningPieces(board);
		}

		@SuppressWarnings("deprecation")
		public boolean tryPlace(Team team, int column) {
			if (!match.isStarted()) {
				return false;
			}

			if (isEnding)
				return false;

			if (turnComplete) {
				team.broadcast(match, "&cYour turn is already over");
				return false;
			}

			if (!team.equals(turnTeam)) {
				team.broadcast(match, "&cWait for your turn");
				return false;
			}

			int emptyRow = getEmptyRow(column);
			Minigames.debug("[Connect4] Row: " + emptyRow);

			if (emptyRow < 0) {
				team.broadcast(match, "&cThis column is full");
				return false;
			}

			turnComplete = true;

			final InARowPiece piece = getPiece(emptyRow, column);
			piece.setTeam(team);

			final World world = arena.getWorld();
			final Material concretePowder = team.getColorType().getConcretePowder();
			final BlockData blockData = Bukkit.createBlockData(concretePowder);

			final Region columnPlaceRegion = arena.getRegion("place_" + column);
			final Region floorRegion = arena.getRegion("reset_floor");

			final int pieceHeight = columnPlaceRegion.getHeight();
			final int floorY = floorRegion.getMinimumY() + 1;

			arena.worldedit()
				.getBlocks(columnPlaceRegion)
				.forEach(block -> {
					final Location location = block.getLocation().toCenterLocation();
					Minigames.debug("[Connect4] Spawning falling block at: " + location + " in column " + column);

					int wait = (block.getY() % 10) * 5;
					Tasks.wait(wait, () -> {
						FallingBlock fallingBlock = world.spawnFallingBlock(location, blockData);
						fallingBlock.setDropItem(false);
						fallingBlock.setInvulnerable(true);
					});

					int y = floorY + ((HEIGHT - (emptyRow + 1)) * pieceHeight) + ((int) location.getY() - columnPlaceRegion.getMinimumY());
					final Location finalLocation = new Location(world, location.getX(), y, location.getZ());
					piece.getLocations().add(finalLocation);
				});

			Minigames.debug("[Connect4] Placed, checking win");
			if (solver.checkWin(board)) {
				winnerTeam = team;
				match.broadcast(team.getAliveMinigamers(match).getFirst().getColoredName() + "&3 has connected 4!");
				match.scored(team);
			}

			Minigames.debug("[Connect4] checking full");
			if (solver.checkFull(board)) {
				match.end();
			}

			return true;
		}
	}

	public long end() {
		isEnding = true;

		WorldEditUtils worldedit = arena.worldedit();
		Region regionFloor = arena.getRegion("reset_floor");

		Material teamMaterial = winnerTeam.getColorType().getConcretePowder(); // winnerTeam was never set if someone quits
		List<InARowPiece> winningPieces = board.getWinningPieces();

		final Consumer<Material> setWinningPeices = material -> {
			for (InARowPiece piece : winningPieces)
				for (Location location : piece.getLocations())
					location.getBlock().setType(material);
		};

		AtomicLong wait = new AtomicLong(TickTime.SECOND.x(3));

		for (int i = 0; i < 3; i++) {
			match.getTasks().wait(wait.getAndAdd(TickTime.TICK.x(15)), () -> setWinningPeices.accept(Material.LIME_CONCRETE_POWDER));
			match.getTasks().wait(wait.getAndAdd(TickTime.TICK.x(15)), () -> setWinningPeices.accept(teamMaterial));
		}

		match.getTasks().wait(wait.get(), () -> worldedit.getBlocks(regionFloor).forEach(block -> block.setType(Material.AIR)));
		match.getTasks().wait(wait.addAndGet(TickTime.SECOND.x(4)), () -> worldedit.getBlocks(regionFloor).forEach(block -> block.setType(Material.YELLOW_WOOL)));

		return wait.get();
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

		public boolean checkFull(InARowPiece[][] board) {
			for (int row = 0; row < HEIGHT; row++) {
				for (int column = 0; column < WIDTH; column++) {
					InARowPiece piece = board[row][column];
					if (piece.isEmpty())
						return false;
				}
			}

			return true;
		}

		public boolean checkWin(InARowPiece[][] board) {
			for (int row = 0; row < this.HEIGHT; row++) { // rows: bottom -> top
				for (int column = 0; column < this.WIDTH; column++) { // columns: left -> right
					if (board[row][column] == null || board[row][column].isEmpty()) {
						continue;
					}

					if (column + (this.IN_A_ROW - 1) < this.WIDTH) { // Checks right
						if (this.check(board, row, column, CheckDirection.RIGHT))
							return true;
					}

					if (row + (this.IN_A_ROW - 1) < this.HEIGHT) { // Checks up
						if (this.check(board, row, column, CheckDirection.UP))
							return true;

						if (column + (this.IN_A_ROW - 1) < this.WIDTH) { // Checks Diagonally Up and Right
							if (this.check(board, row, column, CheckDirection.DIAGONAL_RIGHT))
								return true;
						}

						if (column - (this.IN_A_ROW - 1) >= 0) { // Checks Diagonally Up and Left
							if (this.check(board, row, column, CheckDirection.DIAGONAL_LEFT))
								return true;
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
