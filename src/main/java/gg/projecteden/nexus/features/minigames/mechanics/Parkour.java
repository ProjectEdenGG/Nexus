package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.mechanics.common.CheckpointMechanic;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.Scoreboard;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.CheckpointMatchData;
import gg.projecteden.nexus.features.minigames.models.scoreboards.MinigameScoreboard.Type;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Map;

@Scoreboard(teams = false, sidebarType = Type.MINIGAMER)
public class Parkour extends CheckpointMechanic {

	@Override
	public @NotNull String getName() {
		return "Parkour";
	}

	@Override
	public @NotNull String getDescription() {
		return "Hop your way to the finish";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.FEATHER);
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		event.getMatch().getTasks().repeat(1, 1, () -> event.getMatch().getScoreboard().update());
		super.onStart(event);
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		event.showDeathMessage(false);
		super.onDeath(event);
	}

	@Override
	public @NotNull Map<String, Integer> getScoreboardLines(@NotNull Minigamer minigamer) {
		CheckpointMatchData matchData = getMatchData(minigamer);
		Instant now = Instant.now();

		return Map.of(
			"&a", 8,
			"&3Total Time", 7,
			"&a&e" + matchData.formatTotalLiveTime(minigamer, now), 6,
			"&3Personal Best", 5, // TODO: allow players to decide what to compare against (i.e. PB, SoB, or WR)
			"&b" + matchData.formatTotalBestTime(minigamer), 4,
			"&b", 3,
			"&3Checkpoint Time", 2,
			"&c&e" + matchData.formatSplitTime(minigamer, now), 1
//			"&3Personal Best", 3, | TODO: create a ParkourArena which stores data about valid checkpoint paths so checkpoint splits can be tracked
//			"&d&e" + <code>, 2,
//			"<delta stuff>", 1,
		);
	}
}
