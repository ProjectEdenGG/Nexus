package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchStatisticsClass;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.TicTacToeMatchData;
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
		return "TODO";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.WHITE_CONCRETE);
	}

	@Override
	public boolean isTestMode() {
		return true;
	}

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		super.onInitialize(event);

		Match match = event.getMatch();
		Arena arena = match.getArena();

		// TODO or delete
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
		Minigames.broadcast(winner.getColoredName() + " has won &e" + match.getArena().getDisplayName());
	}

	@Override
	public void end(@NotNull Match match) {
		TicTacToeMatchData matchData = match.getMatchData();
		Tasks.wait(matchData.end() + TickTime.SECOND.get(), () -> super.end(match));
	}

}
