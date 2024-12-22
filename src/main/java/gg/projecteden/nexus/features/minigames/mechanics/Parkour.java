package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.mechanics.common.CheckpointMechanic;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.Scoreboard;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.CheckpointMatchData;
import gg.projecteden.nexus.features.minigames.models.scoreboards.MinigameScoreboard.Type;
import gg.projecteden.nexus.models.checkpoint.MiniCheckpointTimeWrapper;
import gg.projecteden.nexus.models.checkpoint.Pace;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.LinkedHashMap;

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
	public boolean doesAllowSpectating(Match match) {
		return true;
	}

	@Override
	public boolean useScoreboardNumbers(Match match) {
		return false;
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Minigamer minigamer) {
		CheckpointMatchData matchData = getMatchData(minigamer);
		Instant now = Instant.now();

		// get checkpoint time
		MiniCheckpointTimeWrapper cpTime = matchData.getNextSplitTime(minigamer);
		StringBuilder cpTimeHeader = new StringBuilder("&b&3Personal Best");
		if (cpTime != null)
			cpTimeHeader.append("&7 (").append(cpTime.getShortDisplayName()).append(")");

		// get pace
		Pace pace = matchData.getPace(minigamer);

		// this map grew too large for Map.of() lol
		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>(15);
		lines.put("&a", 15);
		lines.put("&3Total Time", 14);
		lines.put("&a&e" + matchData.formatTotalLiveTime(minigamer, now), 13);
		lines.put("&a&3Personal Best", 12);
		lines.put("&b" + matchData.formatTotalBestTime(minigamer), 11);
		lines.put("&b", 10);
		lines.put("&3Checkpoint Time", 9);
		lines.put("&c&e" + matchData.formatSplitTime(minigamer, now), 8);
		lines.put(cpTimeHeader.toString(), 7); // TODO: this isn't technically their best *ever* time, just their time from their best overall run. not sure what a better label would be atm.
		lines.put("&d&e" + matchData.formatSplitBestTime(minigamer, cpTime), 6);
		lines.put("&c", 5);
		lines.put(pace.header(), 4);
		lines.put("&f" + pace.body(), 3);
		lines.put("&d", 2);
		lines.put("&c/mgm leaderboard", 1);
		return lines;
	}
}
