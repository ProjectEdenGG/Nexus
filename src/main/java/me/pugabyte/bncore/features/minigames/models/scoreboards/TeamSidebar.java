package me.pugabyte.bncore.features.minigames.models.scoreboards;

import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.BNScoreboard;

import java.util.HashMap;
import java.util.Map;

public class TeamSidebar implements MinigameScoreboard {
	private Match match;
	private Map<Team, BNScoreboard> scoreboards = new HashMap<>();

	public TeamSidebar(Match match) {
		this.match = match;
	}

	private BNScoreboard createScoreboard(Team team) {
		return new BNScoreboard(
				team.getName() + "-" + match.getArena().getName(),
				match.getArena().getMechanic().getScoreboardTitle(match)
		);
	}

	@Override
	public void update() {
		match.getArena().getTeams().forEach(team ->
				scoreboards.computeIfAbsent(team, this::createScoreboard));

		scoreboards.forEach((team, scoreboard) -> {
			scoreboard.setTitle(match.getArena().getMechanic().getScoreboardTitle(match));
			scoreboard.setLines(match.getArena().getMechanic().getScoreboardLines(match, team));
		});

		match.getArena().getTeams().forEach(team ->
				team.getMembers(match).forEach(minigamer ->
						scoreboards.get(team).addPlayer(minigamer.getPlayer())));
	}

	@Override
	public void handleJoin(Minigamer minigamer) {
		if (minigamer.getTeam() != null)
			scoreboards.computeIfAbsent(minigamer.getTeam(), this::createScoreboard);
	}

	@Override
	public void handleQuit(Minigamer minigamer) {
		if (minigamer.getTeam() != null)
			if (scoreboards.containsKey(minigamer.getTeam()))
				scoreboards.get(minigamer.getTeam()).removePlayer(minigamer.getPlayer());
	}

	@Override
	public void handleEnd() {
		scoreboards.forEach((team, scoreboard) -> scoreboard.delete());
		scoreboards.clear();
	}

}
