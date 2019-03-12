package me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless;

import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.MultiplayerMechanic;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class TeamlessMechanic extends MultiplayerMechanic {

	@Override
	public List<Minigamer> balance(List<Minigamer> minigamers) {
		Arena arena = minigamers.get(0).getMatch().getArena();

		minigamers.forEach(minigamer -> minigamer.setTeam(arena.getTeams().get(0)));

		return minigamers;
	}

	@Override
	public void announceWinners(Match match) {
		Arena arena = match.getArena();
		Map<Minigamer, Integer> scores = new HashMap<>();

		match.getMinigamers().forEach(minigamer -> scores.put(minigamer, minigamer.getScore()));
		int winningScore = getWinningScore(scores);
		List<Minigamer> winners = getWinners(winningScore, scores);

		String announcement;
		if (winningScore == 0) {
			announcement = "No players scored in " + arena.getName();
			match.broadcast(announcement);
		} else {
			if (match.getMinigamers().size() == winners.size()) {
				announcement = "All players tied in " + arena.getName();
			} else {
				announcement = getWinnersString(winners) + arena.getName();
			}
			match.broadcast(announcement + " (" + winningScore + ")");
		}
	}

	private String getWinnersString(List<Minigamer> winners) {
		if (winners.size() > 1) {
			String result = winners.stream()
					.map(minigamer -> minigamer.getTeam().getColor() + minigamer.getPlayer().getName() + ChatColor.DARK_AQUA)
					.collect(Collectors.joining(", "));
			int lastCommaIndex = result.lastIndexOf(", ");
			if (lastCommaIndex >= 0) {
				result = new StringBuilder(result).replace(lastCommaIndex, lastCommaIndex + 2, " and ").toString();
			}
			return result + " have tied in ";
		} else {
			return winners.get(0).getTeam().getColor() + winners.get(0).getPlayer().getName() + ChatColor.DARK_AQUA + " has won ";
		}
	}

	private List<Minigamer> getWinners(int winningScore, Map<Minigamer, Integer> scores) {
		List<Minigamer> winners = new ArrayList<>();

		for (Minigamer minigamer : scores.keySet()) {
			if (scores.get(minigamer).equals(winningScore)) {
				winners.add(minigamer);
			}
		}

		return winners;
	}

	@Override
	public void checkIfShouldBeOver(Match match) {
		if (match.getMinigamers().size() == 1) {
			match.end();
		}
	}
}
