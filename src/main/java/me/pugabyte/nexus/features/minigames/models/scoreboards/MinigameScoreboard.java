package me.pugabyte.nexus.features.minigames.models.scoreboards;

import lombok.Getter;
import lombok.SneakyThrows;
import me.lucko.helper.scoreboard.ScoreboardTeam;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.features.minigames.models.annotations.Scoreboard;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public interface MinigameScoreboard {

	void update();

	void handleJoin(Minigamer minigamer);

	void handleQuit(Minigamer minigamer);

	void handleEnd();

	class Factory {
		@SneakyThrows
		public static MinigameScoreboard create(Match match) {
			Scoreboard annotation = match.getMechanic().getAnnotation(Scoreboard.class);
			Class<? extends MinigameScoreboard> type = Type.MATCH.getType();
			if (annotation != null)
				type = annotation.sidebarType().getType();

			if (type != null)
				return type.getDeclaredConstructor(Match.class).newInstance(match);

			return null;
		}
	}

	enum Type {
		NONE(null),
		MATCH(MatchSidebar.class),
		TEAM(TeamSidebar.class),
		MINIGAMER(MinigamerSidebar.class);

		@Getter
		private Class<? extends MinigameScoreboard> type;

		Type(Class<? extends MinigameScoreboard> clazz) {
			this.type = clazz;
		}
	}

	class Teams implements MinigameScoreboard {
		private Match match;
		private Map<Team, ScoreboardTeam> scoreboardTeams = new HashMap<>();

		public Teams(Match match) {
			this.match = match;
		}

		public static class Factory {
			@SneakyThrows
			public static Teams create(Match match) {
				Scoreboard annotation = match.getMechanic().getAnnotation(Scoreboard.class);
				boolean doTeams = annotation == null || annotation.teams();

				if (doTeams)
					return new Teams(match);

				return null;
			}
		}

		public ScoreboardTeam getScoreboardTeam(Team team) {
			scoreboardTeams.computeIfAbsent(team, $ -> {
				ScoreboardTeam scoreboardTeam = Minigames.getScoreboard().createTeam(match.getArena().getName() + "-" + team.getColoredName(), false);
				scoreboardTeam.setColor(ColorType.toBukkit(team.getColor()));
				scoreboardTeam.setPrefix(team.getColor().toString());
				return scoreboardTeam;
			});

			return scoreboardTeams.get(team);
		}

		@Override
		public void update() {
			match.getMinigamers().forEach(minigamer -> {
				if (minigamer.getTeam() != null)
					getScoreboardTeam(minigamer.getTeam());
			});

			scoreboardTeams.forEach((team, scoreboardTeam) -> {
				team.getAliveMinigamers(match).forEach(minigamer -> scoreboardTeam.addPlayer(minigamer.getPlayer()));
				Bukkit.getOnlinePlayers().forEach(scoreboardTeam::subscribe);
			});
		}

		@Override
		public void handleJoin(Minigamer minigamer) {
			if (minigamer.getTeam() == null) return;
			ScoreboardTeam scoreboardTeam = getScoreboardTeam(minigamer.getTeam());
			scoreboardTeam.addPlayer(minigamer.getPlayer());
			Bukkit.getOnlinePlayers().forEach(scoreboardTeam::subscribe);
		}

		@Override
		public void handleQuit(Minigamer minigamer) {
			if (minigamer.getTeam() == null) return;
			scoreboardTeams.forEach((team, scoreboardTeam) ->
					scoreboardTeam.removePlayer(minigamer.getPlayer()));
		}

		@Override
		public void handleEnd() {
			scoreboardTeams.forEach((team, scoreboardTeam) -> {
				Bukkit.getOnlinePlayers().forEach(player -> {
					scoreboardTeam.removePlayer(player);
					scoreboardTeam.unsubscribe(player);
				});

				Minigames.getScoreboard().removeTeam(scoreboardTeam.getId());
			});
		}

	}

}
