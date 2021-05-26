package me.pugabyte.nexus.features.minigames.models.scoreboards;

import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.utils.EdenScoreboard;

import java.util.HashMap;
import java.util.Map;

public class TeamSidebar implements MinigameScoreboard {
	private final Match match;
	private final Map<Team, EdenScoreboard> scoreboards = new HashMap<>();

	public TeamSidebar(Match match) {
		this.match = match;
	}

	private EdenScoreboard createScoreboard(Team team) {
		return new EdenScoreboard(
				team.getName() + "-" + match.getArena().getName(),
				match.getMechanic().getScoreboardTitle(match)
		);
	}

	@Override
	public void update() {
		match.getArena().getTeams().forEach(team ->
				scoreboards.computeIfAbsent(team, this::createScoreboard));

		scoreboards.forEach((team, scoreboard) -> {
			scoreboard.setTitle(match.getMechanic().getScoreboardTitle(match));
			scoreboard.setLines(match.getMechanic().getScoreboardLines(match, team));
		});

		match.getArena().getTeams().forEach(team ->
				team.getAliveMinigamers(match).forEach(minigamer ->
						scoreboards.get(team).subscribe(minigamer.getPlayer())));
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
				scoreboards.get(minigamer.getTeam()).unsubscribe(minigamer.getPlayer());
	}

	@Override
	public void handleEnd() {
		scoreboards.forEach((team, scoreboard) -> scoreboard.delete());
		scoreboards.clear();
	}

}
