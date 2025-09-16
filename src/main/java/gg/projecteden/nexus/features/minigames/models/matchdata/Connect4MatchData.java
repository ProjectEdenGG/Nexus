package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.mechanics.Connect4;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.matchdata.shared.InARowBoard;
import gg.projecteden.nexus.features.minigames.models.matchdata.shared.InARowBoard.InARowPiece;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Data
@MatchDataFor(Connect4.class)
public class Connect4MatchData extends MatchData {
	private final Connect4Board board = new Connect4Board();
	protected boolean turnComplete;

	public Connect4MatchData(Match match) {
		super(match);
	}

	public class Connect4Board extends InARowBoard {
		public Connect4Board() {
			super(6, 7, 4);
		}

		public int getEmptyRow(int column) {
			Integer finalRow = null;
			for (int row = (height - 1); row >= 0; row--)
				if (getPiece(row, column).isEmpty()) {
					finalRow = row;
					break;
				}

			if (finalRow == null)
				throw new InvalidInputException("That column is full");

			return finalRow;
		}

		@SuppressWarnings("deprecation")
		public void tryPlace(Team team, int column) {
			Minigames.debug("[Connect4] Placing...");
			if (!match.isStarted())
				return;

			if (isEnding)
				return;

			if (turnComplete) {
				team.broadcast(match, "&cYour turn is already over");
				return;
			}

			if (!team.equals(turnTeam)) {
				team.broadcast(match, "&cWait for your turn");
				return;
			}

			int emptyRow = getEmptyRow(column);
			Minigames.debug("[Connect4] Row: " + emptyRow);

			if (emptyRow < 0) {
				team.broadcast(match, "&cThis column is full");
				return;
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

					int y = floorY + ((height - (emptyRow + 1)) * pieceHeight) + ((int) location.getY() - columnPlaceRegion.getMinimumY());
					final Location finalLocation = new Location(world, location.getX(), y, location.getZ());
					piece.getLocations().add(finalLocation);
				});

			Minigames.debug("[Connect4] Placed, checking win");
			if (solver.checkWin()) {
				winnerTeam = team;
				match.broadcast(team.getAliveMinigamers(match).getFirst().getColoredName() + "&3 has connected 4!");
				match.scored(team);
			}

			Minigames.debug("[Connect4] checking full");
			if (solver.checkFull()) {
				match.end();
				return;
			}

			match.getTasks().wait(TickTime.SECOND.x(2), () -> {
				Minigames.debug("[Connect4] Next Turn");
				match.<TeamMechanic>getMechanic().nextTurn(match);
			});
		}
	}

	public long end() {
		isEnding = true;

		var worldedit = arena.worldedit();
		var regionFloor = arena.getRegion("reset_floor");
		var wait = new AtomicLong(TickTime.SECOND.x(3));

		if (winnerTeam != null) {
			Material teamMaterial = winnerTeam.getColorType().getConcretePowder();
			List<InARowPiece> winningPieces = board.getWinningPieces();

			final Consumer<Material> setWinningPeices = material -> {
				for (InARowPiece piece : winningPieces)
					for (Location location : piece.getLocations())
						location.getBlock().setType(material);
			};

			for (int i = 0; i < 3; i++) {
				match.getTasks().wait(wait.getAndAdd(TickTime.TICK.x(15)), () -> setWinningPeices.accept(Material.LIME_CONCRETE_POWDER));
				match.getTasks().wait(wait.getAndAdd(TickTime.TICK.x(15)), () -> setWinningPeices.accept(teamMaterial));
			}
		}

		match.getTasks().wait(wait.get(), () -> worldedit.getBlocks(regionFloor).forEach(block -> block.setType(Material.AIR)));
		match.getTasks().wait(wait.addAndGet(TickTime.SECOND.x(4)), () -> worldedit.getBlocks(regionFloor).forEach(block -> block.setType(Material.YELLOW_WOOL)));

		return wait.get();
	}

	public class Connect4Evaluator {

		private static final int ROWS = 6;
		private static final int COLS = 7;

		public static EvaluationResult evaluateBoard(InARowPiece[][] board, Team team1, Team team2) {
			int score = 0;

			// Horizontal
			for (int r = 0; r < ROWS; r++) {
				for (int c = 0; c < COLS - 3; c++) {
					score += evaluateWindow(board, r, c, 0, 1, team1, team2);
				}
			}

			// Vertical
			for (int r = 0; r < ROWS - 3; r++) {
				for (int c = 0; c < COLS; c++) {
					score += evaluateWindow(board, r, c, 1, 0, team1, team2);
				}
			}

			// Diagonal down-right
			for (int r = 0; r < ROWS - 3; r++) {
				for (int c = 0; c < COLS - 3; c++) {
					score += evaluateWindow(board, r, c, 1, 1, team1, team2);
				}
			}

			// Diagonal up-right
			for (int r = 3; r < ROWS; r++) {
				for (int c = 0; c < COLS - 3; c++) {
					score += evaluateWindow(board, r, c, -1, 1, team1, team2);
				}
			}

			return scoreToPercentage(score);
		}

		private static int evaluateWindow(InARowPiece[][] board, int row, int col, int dr, int dc, Team team1, Team team2) {
			int t1 = 0, t2 = 0, empty = 0;

			for (int i = 0; i < 4; i++) {
				InARowPiece piece = board[row + i * dr][col + i * dc];
				if (piece.isEmpty()) {
					empty++;
				} else if (piece.getTeam() == team1) {
					t1++;
				} else if (piece.getTeam() == team2) {
					t2++;
				}
			}

			if (t1 > 0 && t2 > 0) return 0; // blocked

			if (t1 == 4) return +1000;
			if (t1 == 3 && empty == 1) return +50;
			if (t1 == 2 && empty == 2) return +10;
			if (t1 == 1 && empty == 3) return +1;

			if (t2 == 4) return -1000;
			if (t2 == 3 && empty == 1) return -50;
			if (t2 == 2 && empty == 2) return -10;
			if (t2 == 1 && empty == 3) return -1;

			return 0;
		}

		private static EvaluationResult scoreToPercentage(int score) {
			double normalized = 1.0 / (1.0 + Math.exp(-score / 200.0));
			int t1Percent = (int) Math.round(normalized * 100);
			int t2Percent = 100 - t1Percent;

			return new EvaluationResult(score, t1Percent, t2Percent);
		}

		public static class EvaluationResult {
			public final int score;
			public final int team1Percent;
			public final int team2Percent;

			public EvaluationResult(int score, int t1, int t2) {
				this.score = score;
				this.team1Percent = t1;
				this.team2Percent = t2;
			}

			@Override
			public String toString() {
				return String.format("Score=%d | Team1=%d%% | Team2=%d%%",
					score, team1Percent, team2Percent);
			}
		}
	}

}
