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
			"&a", 10,
			"&3Total Time", 9,
			"&a&e" + matchData.formatTotalLiveTime(minigamer, now), 8,
			"&3Personal Best", 7,
			"&b" + matchData.formatTotalBestTime(minigamer), 6,
			"&b", 5,
			"&3Checkpoint Time", 4,
			"&c&e" + matchData.formatSplitTime(minigamer, now), 3,
//			"&3Personal Best", 5,
//			"&d&e" + <code>, 4,
//			"<delta stuff>", 3,
			"&c", 2,
			"&6/mgm leaderboard", 1
		);
	}
}
