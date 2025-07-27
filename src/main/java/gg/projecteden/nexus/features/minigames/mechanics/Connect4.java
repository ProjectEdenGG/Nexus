package gg.projecteden.nexus.features.minigames.mechanics;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchStatisticsClass;
import gg.projecteden.nexus.features.minigames.models.arenas.Connect4Arena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.Connect4MatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MatchStatisticsClass(MatchStatistics.class)
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
		Connect4MatchData matchData = match.getMatchData();
		matchData.setTurnComplete(false);
		super.onTurnStart(match, team);
	}

	@Override
	public void announceWinners(@NotNull Match match) {
		Connect4MatchData matchData = match.getMatchData();
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
		Connect4MatchData matchData = match.getMatchData();
		Tasks.wait(matchData.end() + TickTime.SECOND.get(), () -> super.end(match));
	}

	@EventHandler
	public void on(EntityRemoveFromWorldEvent event) {
		if (!ArenaManager.LOADED)
			return;

		var entity = event.getEntity();
		var location = entity.getLocation();
		var arena = ArenaManager.getFromLocation(entity.getLocation());

		if (arena == null)
			return;

		if (!(arena.getMechanic() instanceof Connect4))
			return;

		if (!(entity instanceof FallingBlock fallingBlock))
			return;

		if (!arena.isInRegion(location, "board"))
			return;

		entity.getWorld().setBlockData(location, fallingBlock.getBlockData());
	}

}
