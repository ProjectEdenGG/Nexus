package me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Time;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
		match.getMinigamers().stream().filter(Minigamer::isAlive).forEach(minigamer -> teams.add(minigamer.getTeam()));
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

	public boolean basicBalanceCheck(List<Minigamer> minigamers) {
		if (minigamers.isEmpty())
			return false;

		Match match = minigamers.get(0).getMatch();
		Arena arena = match.getArena();
		List<Team> teams = new ArrayList<>(arena.getTeams());

		int required = 0;
		for (Team team : teams) required += team.getMinPlayers();

		if (match.getMinigamers().size() < required) {
			criticalErrorAbort("Not enough players to meet team requirements!", match);
			return false;
		}

		return true;
	}

	@Override
	public void balance(List<Minigamer> minigamers) {
		minigamers = new ArrayList<>(minigamers); // cries in pass by reference
		if (!basicBalanceCheck(minigamers))
			return;

		minigamers.forEach(minigamer -> minigamer.setTeam(null)); // clear teams
		Collections.shuffle(minigamers); // lets us assign teams to players in random order
		Match match = minigamers.get(0).getMatch();
		List<Team> teams = new ArrayList<>(match.getArena().getTeams()); // old code made a new list so im doing it too

		// only one team, no need to bother with math
		if (teams.size() == 1) {
			minigamers.forEach(minigamer -> minigamer.setTeam(teams.get(0)));
			return;
		}

		// create wrapper objects
		// ALL PERCENTAGES HERE RANGE FROM 0 to 1 !!
		List<BalanceWrapper> wrappers = new ArrayList<>();
		double percentageSum = 0; // sum of all balance percentages
		int noPercentage = 0; // count of teams w/o balance percentages
		for (Team team : teams) {
			BalanceWrapper wrapper = new BalanceWrapper(team, match);
			wrappers.add(wrapper);
			if (wrapper.getPercentage() != null)
				percentageSum += wrapper.getPercentage();
			else
				noPercentage++;
		}

		if (noPercentage > 0 && percentageSum < 1) {
			// evenly split the balance of teams that don't have a balance percentage (if there is any unassigned %)
			double percentage = (1d/noPercentage) * (1d-percentageSum);
			wrappers.stream().filter(wrapper -> wrapper.getPercentage() == null).forEach(wrapper -> wrapper.setPercentage(percentage));
		}

		// ensure percentages add up to 100
		double totalPercentage = wrappers.stream().mapToDouble(BalanceWrapper::getPercentage).sum();
		wrappers.forEach(wrapper -> wrapper.setPercentage(wrapper.getPercentage() / totalPercentage));

		// add players to teams that need them (i.e. have a minimum player count that is not satisfied)
		while (!minigamers.isEmpty()) {
			Optional<BalanceWrapper> needsPlayers = wrappers.stream().filter(e -> e.getNeededPlayers() > 0).findFirst();
			if (!needsPlayers.isPresent())
				break;
			Team team = needsPlayers.get().getTeam();
			minigamers.remove(0).setTeam(team);
		}

		// add rest of players according to percentages
		while (!minigamers.isEmpty()) {
			// this basically finds the team with the largest percent
			wrappers = wrappers.stream().filter(wrapper -> wrapper.getNeededPlayers() != -1).sorted().collect(Collectors.toList());
			if (wrappers.isEmpty())
				break;
			// get teams with matching percentage discrepancies (ie the teams are perfectly balanced) and randomly
			//  select one of them
			List<BalanceWrapper> equalWrappers = new ArrayList<>();
			equalWrappers.add(wrappers.get(0));
			int c = 1;
			double val = wrappers.get(0).percentageDiscrepancy();
			while (c < wrappers.size() && Math.abs(wrappers.get(c).percentageDiscrepancy() - val) < 0.0001d) {
				equalWrappers.add(wrappers.get(c));
				c++;
			}
			Team team = RandomUtils.randomElement(equalWrappers).getTeam();
			minigamers.remove(0).setTeam(team);
		}

		// leftover players means the teams all (somehow) reached their max player count
		minigamers.forEach(minigamer -> {
			minigamer.tell("Could not assign you to a team!");
			minigamer.quit();
		});
	}

	@Data
	@EqualsAndHashCode
	public static class BalanceWrapper implements Comparable<BalanceWrapper> {
		private final Team team;
		private final Match match;
		private Double percentage;
		private BalanceWrapper(Team team, Match match) {
			this.team = team;
			this.match = match;
			if (team.getBalancePercentage() == -1)
				percentage = null;
			else
				percentage = team.getBalancePercentage()/100d;
		}
		public int getNeededPlayers() {
			int teamPlayers = team.getMinigamers(match).size();
			if (team.getMaxPlayers() > -1 && teamPlayers >= team.getMaxPlayers())
				return -1;
			return Math.max(0, team.getMinPlayers()-teamPlayers);
		}

		/**
		 * Calculates the difference between the team's specified percentage and its current percentage (i.e. the
		 * current balance of the match). A negative score is unbalanced in favor of the team, a positive score is
		 * unbalanced in favor of other teams. Larger scores mean more unbalanced.
		 * @return a score ranging from -1 to 1
		 */
		public double percentageDiscrepancy() {
			int totalPlayers = (int) match.getMinigamers().stream().filter(minigamer -> minigamer.getTeam() != null).count();
			int teamPlayers = team.getMinigamers(match).size();

			double matchPercentage;
			if (totalPlayers == 0)
				matchPercentage = 0; // this is the first added player, assume all teams are on 0%
			else
				matchPercentage = (double)teamPlayers/totalPlayers; // get % of players on this team
			return percentage-matchPercentage;
		}

		/**
		 * Compares which of two teams has a larger player discrepancy. A negative value means this team has a larger
		 * discrepancy (to allow for naturally sort in descending order)
		 * @param balanceWrapper the other team
		 * @return a score ranging from -1 to 1
		 */
		@Override
		public int compareTo(@NotNull BalanceWrapper balanceWrapper) {
			return (int) ((balanceWrapper.percentageDiscrepancy()-percentageDiscrepancy())*100);
		}
	}
}
