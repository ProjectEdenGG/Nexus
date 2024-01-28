package gg.projecteden.nexus.features.minigames.models.scoreboards;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.parchment.sidebar.Sidebar;
import gg.projecteden.parchment.sidebar.SidebarLayout;
import gg.projecteden.parchment.sidebar.SidebarStage;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TeamSidebar implements MinigameScoreboard {
	private final Match match;
	private final Map<Team, TeamSidebarLayout> scoreboards = new HashMap<>();

	public TeamSidebar(Match match) {
		this.match = match;
	}

	private TeamSidebarLayout createScoreboard(Team team) {
		return new TeamSidebarLayout(team);
	}

	@Override
	public void update() {
		match.getArena().getTeams().forEach(team ->
				scoreboards.computeIfAbsent(team, this::createScoreboard));

		match.getArena().getTeams().forEach(team ->
				team.getAliveMinigamers(match).forEach(minigamer ->
					Sidebar.get(minigamer.getPlayer()).applyLayout(scoreboards.get(team))));

		scoreboards.forEach((team, scoreboard) -> scoreboard.refresh());
	}

	@Override
	public void handleJoin(Minigamer minigamer) {
		if (minigamer.getTeam() != null)
			scoreboards.computeIfAbsent(minigamer.getTeam(), this::createScoreboard);
		update();
	}

	@Override
	public void handleQuit(Minigamer minigamer) {
		if (minigamer.getTeam() != null)
			if (scoreboards.containsKey(minigamer.getTeam()))
				Sidebar.get(minigamer.getPlayer()).applyLayout(null);
		update();
	}

	@Override
	public void handleEnd() {
		match.getOnlinePlayers().forEach(player -> Sidebar.get(player).applyLayout(null));
		scoreboards.clear();
	}

	@AllArgsConstructor
	public class TeamSidebarLayout extends SidebarLayout {

		Team team;

		@Override
		protected void setup(SidebarStage stage) {
			stage.setTitle(match.getMechanic().getScoreboardTitle(match));

			AtomicInteger lineNum = new AtomicInteger();
			match.getMechanic().getScoreboardLines(match, team).forEach((line, score) -> {
				if (lineNum.get() >= 15) return;
				stage.setLine(lineNum.getAndIncrement(), line, match.getMechanic().useScoreboardNumbers(match) && score != Integer.MIN_VALUE ? "&c" + score : null);
			});

			for (int i = lineNum.get(); i < 15; i++)
				stage.setLine(i, null);
		}

		@Override
		protected void update(SidebarStage stage) {
			setup(stage);
		}
	}

}
