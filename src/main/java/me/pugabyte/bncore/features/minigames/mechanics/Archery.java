package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.ArcheryMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

// TODO: Only spawn targets for the active islands
// TODO: On Join --> load the next range color locations into matchdata

public class Archery extends TeamlessMechanic {
	@Override
	public String getName() {
		return "Archery";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.BOW);
	}

	@Override
	public boolean shuffleSpawnpoints() {
		return false;
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		// Start target task
	}

	// TODO: Show all players scores on scoreboard before match ends?
	@Override
	public Map<String, Integer> getScoreboardLines(Match match) {
		Map<String, Integer> lines = new HashMap<>();
		ArcheryMatchData matchData = match.getMatchData();

		if (match.isStarted()) {
			int timeLeft = match.getTimer().getTime();
			for (Minigamer minigamer : match.getMinigamers()) {
				lines.put("&1", 0);
				lines.put("&2&fScore: &c&l" + minigamer.getScore(), 0);
				lines.put("&3&fTargets Hit: &c&l" + matchData.getTargetsHit(minigamer), 0);
				lines.put("&4", 0);
				lines.put("&5&fTime Left: &c&l" + timeLeft, 0);
			}
		} else {
			for (Minigamer minigamer : match.getMinigamers())
				lines.put(minigamer.getColoredName(), 0);
		}

		return lines;
	}
}
