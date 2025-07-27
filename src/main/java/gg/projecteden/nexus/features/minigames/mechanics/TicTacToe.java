package gg.projecteden.nexus.features.minigames.mechanics;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchStatisticsClass;
import gg.projecteden.nexus.features.minigames.models.arenas.TicTacToeArena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.TicTacToeMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.shared.InARowBoard;
import gg.projecteden.nexus.features.minigames.models.matchdata.shared.InARowBoard.InARowPiece;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MatchStatisticsClass(MatchStatistics.class)
public final class TicTacToe extends TeamMechanic {

	@Override
	public @NotNull String getName() {
		return "TicTacToe";
	}

	@Override
	public @NotNull String getDescription() {
		return "A classic game of TicTacToe, place 3 Xs or Os in a row to win";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.QUARTZ_BLOCK);
	}

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		super.onInitialize(event);

		Match match = event.getMatch();
		TicTacToeArena arena = match.getArena();

		match.worldedit().getBlocks(arena.getRegion("board")).forEach(block -> {
			if (Material.LIGHT_GRAY_CONCRETE != block.getType())
				block.setType(Material.AIR);
		});
	}

	@Override
	public void onTurnStart(@NotNull Match match, @NotNull Team team) {
		match.broadcast(team, "Your turn");
		super.onTurnStart(match, team);
	}

	@Override
	public void announceWinners(@NotNull Match match) {
		TicTacToeMatchData matchData = match.getMatchData();
		if (matchData.getWinnerTeam() == null) {
			Minigames.broadcast("Nobody won in &e" + match.getArena().getDisplayName());
			return;
		}

		final Minigamer winner = matchData.getWinnerTeam().getAliveMinigamers(match).get(0);
		match.getMatchStatistics().award(MatchStatistics.WINS, winner);
		Minigames.broadcast(winner.getColoredName() + " &3has won &e" + match.getArena().getDisplayName());
	}

	@Override
	public void end(@NotNull Match match) {
		TicTacToeMatchData matchData = match.getMatchData();
		Tasks.wait(matchData.end() + TickTime.SECOND.get(), () -> super.end(match));
	}

	public enum TicTacToeSign {
		X, O,
		;

		public String getSchematic(Arena arena) {
			return arena.getSchematicName(name().toLowerCase());
		}
	}

	public enum TicTacToeCell {
		TOP_LEFT, TOP_CENTER, TOP_RIGHT,
		CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT,
		BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
		;

		public Region getRegion(Arena arena) {
			return arena.getRegion("cell_" + name().toLowerCase());
		}

		public Region getBackdropRegion(Arena arena) {
			return arena.getRegion("cell_backdrop_" + name().toLowerCase());
		}

		public void paste(Arena arena, TicTacToeSign sign) {
			arena.worldedit().paster()
				.at(getRegion(arena).getMinimumPoint())
				.file(sign.getSchematic(arena))
				.build();
		}

		public void setBackdrop(Arena arena, Material material) {
			for (var block : arena.worldguard().getAllBlocks(getBackdropRegion(arena)))
				arena.worldguard().toLocation(block).getBlock().setType(material);
		}

		public static TicTacToeCell from(InARowPiece piece) {
			int row = piece.getY();
			int column = piece.getX();
			return values()[row * 3 + column];
		}

		public InARowPiece getPiece(InARowBoard board) {
			return board.getBoard()[getRow()][getColumn()];
		}

		public int getRow() {
			return ordinal() / 3;
		}

		public int getColumn() {
			return ordinal() % 3;
		}
	}

}
