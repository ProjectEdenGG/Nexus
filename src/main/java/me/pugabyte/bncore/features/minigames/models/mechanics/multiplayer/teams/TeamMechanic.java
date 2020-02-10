package me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.MultiplayerMechanic;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class TeamMechanic extends MultiplayerMechanic {

	@Override
	public boolean isTeamGame() {
		return true;
	}

	@Override
	public void announceWinners(Match match) {
		Arena arena = match.getArena();
		Map<ChatColor, Integer> scoreList = new HashMap<>();
		Map<Team, Integer> scores = match.getScores();

		int winningScore = getWinningScore(scores.values());
		List<Team> winners = getWinners(winningScore, scores);
		scores.keySet().forEach(team -> scoreList.put(team.getColor(), scores.get(team)));

		String announcement;
		if (winningScore == 0) {
			announcement = "No teams scored in &e" + arena.getDisplayName();
			Minigames.broadcast(announcement);
		} else {
			if (arena.getTeams().size() == winners.size()) {
				announcement = "All teams tied in &e" + arena.getDisplayName();
			} else {
				announcement = getWinnersString(winners) + "&e" + arena.getDisplayName();
			}
			Minigames.broadcast(announcement + getScoreList(scoreList));
		}
	}

	private String getWinnersString(List<Team> winners) {
		if (winners.size() > 1) {
			String result = winners.stream()
					.map(team -> team.getColoredName() + ChatColor.DARK_AQUA)
					.collect(Collectors.joining(", "));
			int lastCommaIndex = result.lastIndexOf(", ");
			if (lastCommaIndex >= 0) {
				result = new StringBuilder(result).replace(lastCommaIndex, lastCommaIndex + 2, " and ").toString();
			}
			return result + " tied in ";
		} else {
			return winners.get(0).getColoredName() + " &3won ";
		}
	}

	private List<Team> getWinners(int winningScore, Map<Team, Integer> scores) {
		List<Team> winners = new ArrayList<>();

		for (Team team : scores.keySet()) {
			if (scores.get(team).equals(winningScore)) {
				winners.add(team);
			}
		}

		return winners;
	}

	private String getScoreList(Map<ChatColor, Integer> scores) {
		StringBuilder scoreList = new StringBuilder(" &3( ");
		int counter = 0;
		for (ChatColor color : scores.keySet()) {
			scoreList.append(color)
					.append(scores.get(color).toString())
					.append(ChatColor.DARK_AQUA)
					.append(++counter != scores.size() ? " | " : " )");
		}
		return scoreList.toString();
	}

	Team getSmallestTeam(List<Minigamer> minigamers, List<Team> teams) {
		Map<Team, Integer> assignments = getCurrentAssignments(minigamers, teams);
		return getSmallestTeam(assignments);
	}

	private Team getSmallestTeam(Map<Team, Integer> assignments) {
		Team smallest = null;
		int min = Integer.MAX_VALUE;
		for (Map.Entry<Team, Integer> entry : assignments.entrySet()) {
			if (entry.getValue() < min) {
				smallest = entry.getKey();
				min = entry.getValue();
			}
		}

		return smallest;
	}

	private Map<Team, Integer> getCurrentAssignments(List<Minigamer> minigamers, List<Team> teams) {
		Map<Team, Integer> assignments = new HashMap<>();
		teams.forEach(team -> assignments.put(team, 0));
		minigamers.forEach(minigamer -> {
			if (minigamer.getTeam() != null) {
				assignments.put(minigamer.getTeam(), assignments.get(minigamer.getTeam()) + 1);
			}
		});
		return assignments;
	}

	@Override
	public boolean shouldBeOver(Match match) {
		Set<Team> teams = new HashSet<>();
		match.getMinigamers().forEach(minigamer -> teams.add(minigamer.getTeam()));
		return teams.size() == 1;
	}

}
