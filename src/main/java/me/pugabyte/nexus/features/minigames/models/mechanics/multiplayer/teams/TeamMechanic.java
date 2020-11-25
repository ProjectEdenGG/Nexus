package me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Match.MatchTasks;
import me.pugabyte.nexus.features.minigames.models.Match.MatchTasks.MatchTaskType;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.MultiplayerMechanic;
import me.pugabyte.nexus.utils.Time;
import net.md_5.bungee.api.ChatColor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

		for (Team team : scores.keySet())
			if (scores.getOrDefault(team, 0).equals(winningScore))
				winners.add(team);

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
			if (minigamer.getTeam() != null)
				assignments.put(minigamer.getTeam(), assignments.get(minigamer.getTeam()) + 1);
		});
		return assignments;
	}

	@Override
	public boolean shouldBeOver(Match match) {
		Set<Team> teams = new HashSet<>();
		match.getMinigamers().forEach(minigamer -> teams.add(minigamer.getTeam()));
		if (teams.size() == 1) {
			Nexus.log("Match has only one team left, ending");
			return true;
		}

		int winningScore = match.getArena().getCalculatedWinningScore(match);
		if (winningScore > 0)
			for (Team team : teams)
				if (team.getScore(match) >= winningScore) {
					match.getMatchData().setWinnerTeam(team);
					Nexus.log("Team match has reached calculated winning score (" + winningScore + "), ending");
					return true;
				}

		return false;
	}

	public void onTurnStart(Match match, Team team) {
		match.getMatchData().setTurnStarted(LocalDateTime.now());
	}

	public void onTurnEnd(Match match, Team team) {

	}

	public void nextTurn(Match match) {
		Arena arena = match.getArena();
		MatchData matchData = match.getMatchData();
		MatchTasks tasks = match.getTasks();

		if (match.isEnded() || matchData == null)
			return;

		if (matchData.getTurnTeam() != null) {
			onTurnEnd(match, matchData.getTurnTeam());
			matchData.setTurnTeam(null);
		}

		if (shouldBeOver(match)) {
			end(match);
			return;
		}

		if (matchData.getTurns() >= match.getArena().getMaxTurns()) {
			match.broadcast("Max turns reached, ending game");
			match.end();
			return;
		}

		if (matchData.getTurnTeamList().isEmpty()) {
			matchData.setTurnTeamList(new ArrayList<>(match.getAliveTeams()));
			if (shuffleTurnList())
				Collections.shuffle(matchData.getTurnTeamList());
		}

		tasks.cancel(MatchTaskType.TURN);

		Team team = matchData.getTurnTeamList().get(0);
		matchData.getTurnTeamList().remove(team);
		matchData.setTurnTeam(team);
		match.getScoreboard().update();

		onTurnStart(match, team);
		tasks.register(MatchTaskType.TURN, tasks.wait(arena.getTurnTime() * Time.SECOND.get(), () -> nextTurn(match)));
	}

	@Override
	public void onQuit(MatchQuitEvent event) {
		Match match = event.getMatch();
		Team team = event.getMinigamer().getTeam();
		if (team != null && team.equals(match.getMatchData().getTurnTeam()))
			if (team.getAliveMinigamers(match).size() == 0)
				nextTurn(match);

		super.onQuit(event);
	}

}
