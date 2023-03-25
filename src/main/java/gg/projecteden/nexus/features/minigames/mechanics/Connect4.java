package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.arenas.Connect4Arena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MinigamerQuitEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.Connect4MatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class Connect4 extends TeamMechanic {

	@Override
	public @NotNull String getName() {
		return "Connect4";
	}

	@Override
	public @NotNull String getDescription() {
		return "Connect 4 checkers of your color in a row";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.BLUE_CONCRETE);
	}

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		super.onInitialize(event);

		Match match = event.getMatch();
		Connect4Arena arena = match.getArena();

		match.worldedit().getBlocks(arena.getRegion("board")).forEach(block -> {
			if (!block.getType().equals(Material.YELLOW_WOOL))
				block.setType(Material.AIR);
		});

		match.worldedit().getBlocks(arena.getRegion("reset_floor")).forEach(block -> block.setType(Material.YELLOW_WOOL));
	}

	@Override
	public void onTurnStart(@NotNull Match match, @NotNull Team team) {
		match.broadcast(team, "Your turn");
		super.onTurnStart(match, team);
	}

	@Override
	public void announceWinners(@NotNull Match match) {
		Connect4MatchData matchData = match.getMatchData();
		final Minigamer winner = matchData.getWinnerTeam().getAliveMinigamers(match).get(0);
		Minigames.broadcast(Minigames.PREFIX + winner.getColoredName() + " has won &e" + match.getArena().getDisplayName());
	}

	@Override
	public void end(@NotNull Match match) {
		Connect4MatchData matchData = match.getMatchData();
		Tasks.wait(matchData.end() + TickTime.SECOND.get(), () -> super.end(match));
	}

	@EventHandler
	public void onMatchQuit(MinigamerQuitEvent event) {
		super.onQuit(event);
	}
}
