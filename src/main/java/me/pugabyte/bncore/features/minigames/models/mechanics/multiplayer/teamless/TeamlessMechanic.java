package me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.MultiplayerMechanic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class TeamlessMechanic extends MultiplayerMechanic {

	@Override
	public boolean isTeamGame() {
		return false;
	}

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

		match.getAlivePlayers().forEach(minigamer -> scores.put(minigamer, minigamer.getScore()));
		if (scores.size() == 0) return;
		int winningScore = getWinningScore(scores);
		List<Minigamer> winners = getWinners(winningScore, scores);

		String announcement;
		if (winningScore == 0 && winners.size() != 1)
			announcement = "No players scored in " + arena.getDisplayName();
		else {
			if (match.getAlivePlayers().size() == winners.size() && match.getAlivePlayers().size() > 1)
				announcement = "All players tied in " + arena.getDisplayName();
			else
				announcement = getWinnersString(winners) + "&e" + arena.getDisplayName();
			if (winningScore != 0)
				announcement += " (" + winningScore + ")";
		}
		Minigames.broadcast(announcement);
	}

	private String getWinnersString(List<Minigamer> winners) {
		if (winners.size() > 1) {
			String result = winners.stream()
					.map(minigamer -> minigamer.getColoredName() + "&3")
					.collect(Collectors.joining(", "));
			int lastCommaIndex = result.lastIndexOf(", ");
			if (lastCommaIndex >= 0) {
				result = new StringBuilder(result).replace(lastCommaIndex, lastCommaIndex + 2, " and ").toString();
			}
			return result + " have tied in ";
		} else {
			return winners.get(0).getColoredName() + " &3has won ";
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
	public boolean shouldBeOver(Match match) {
		return match.getAlivePlayers().size() <= 1;
	}

}
