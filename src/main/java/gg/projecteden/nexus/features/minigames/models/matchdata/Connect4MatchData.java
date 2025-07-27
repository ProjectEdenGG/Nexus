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

}
