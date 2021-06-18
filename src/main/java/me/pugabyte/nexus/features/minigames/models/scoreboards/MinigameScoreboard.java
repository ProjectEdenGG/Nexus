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
import me.pugabyte.nexus.utils.PlayerUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
		private final Class<? extends MinigameScoreboard> type;

		Type(Class<? extends MinigameScoreboard> clazz) {
			this.type = clazz;
		}
	}

	interface ITeams extends MinigameScoreboard {
		class Factory {
			@SneakyThrows
			public static ITeams create(Match match) {
				Scoreboard annotation = match.getMechanic().getAnnotation(Scoreboard.class);
				boolean doTeams = annotation == null || annotation.teams();
				boolean nameTagVisibility = annotation == null || annotation.visibleNameTags();

				if (doTeams)
					return new Teams(match);
				else if (!nameTagVisibility)
					return new Teamless(match);
				return null;
			}
		}

		/**
		 * Get the scoreboard team corresponding to a team
		 * @param team a minigame team
		 * @return a scoreboard team
		 */
		ScoreboardTeam getScoreboardTeam(Team team);
	}

	class Teams implements ITeams {
		private final Match match;
		private final Map<Team, ScoreboardTeam> scoreboardTeams = new HashMap<>();

		public Teams(Match match) {
			this.match = match;
		}

		@Override
		public ScoreboardTeam getScoreboardTeam(Team team) {
			scoreboardTeams.computeIfAbsent(team, $ -> {
				ScoreboardTeam scoreboardTeam = Minigames.getScoreboard().createTeam(match.getArena().getName() + "-" + team.getColoredName(), false);
				scoreboardTeam.setColor(Objects.requireNonNull(ColorType.toBukkit(team.getVanillaChatColor())));
				scoreboardTeam.setPrefix(team.getChatColor().toString());
				scoreboardTeam.setNameTagVisibility(team.getNameTagVisibility());
				return scoreboardTeam;
			});

			return scoreboardTeams.get(team);
		}

		@Override
		public void update() {
			match.getMinigamers().forEach(minigamer -> {
				boolean removePlayer = !minigamer.isAlive() || minigamer.getTeam() == null;
				scoreboardTeams.forEach((team, scoreboardTeam) -> {
					if (removePlayer || !minigamer.getTeam().equals(team))
						scoreboardTeam.removePlayer(minigamer.getPlayer());
				});
				if (minigamer.getTeam() != null)
					getScoreboardTeam(minigamer.getTeam()).addPlayer(minigamer.getPlayer());
			});

			scoreboardTeams.values().forEach(scoreboardTeam -> PlayerUtils.getOnlinePlayers().forEach(scoreboardTeam::subscribe));
		}

		@Override
		public void handleJoin(Minigamer minigamer) {
			if (minigamer.getTeam() == null) return;
			ScoreboardTeam scoreboardTeam = getScoreboardTeam(minigamer.getTeam());
			scoreboardTeam.addPlayer(minigamer.getPlayer());
			PlayerUtils.getOnlinePlayers().forEach(scoreboardTeam::subscribe);
		}

		@Override
		public void handleQuit(Minigamer minigamer) {
			if (minigamer.getTeam() == null) return;
			ScoreboardTeam scoreboardTeam = getScoreboardTeam(minigamer.getTeam());
			scoreboardTeam.removePlayer(minigamer.getPlayer());
			PlayerUtils.getOnlinePlayers().forEach(scoreboardTeam::subscribe);
		}

		@Override
		public void handleEnd() {
			scoreboardTeams.forEach((team, scoreboardTeam) -> {
				scoreboardTeam.getPlayers().forEach(scoreboardTeam::removePlayer);
				PlayerUtils.getOnlinePlayers().forEach(scoreboardTeam::unsubscribe);
				Minigames.getScoreboard().removeTeam(scoreboardTeam.getId());
			});
		}

	}

	class Teamless implements ITeams {
		private final ScoreboardTeam scoreboardTeam;
		private final Match match;

		public Teamless(Match match) {
			this.match = match;
			scoreboardTeam = Minigames.getScoreboard().createTeam(match.getArena().getName() + "-default", false);
			Scoreboard annotation = match.getMechanic().getAnnotation(Scoreboard.class);
			ScoreboardTeam.NameTagVisibility visibility = (annotation == null || annotation.visibleNameTags()) ? ScoreboardTeam.NameTagVisibility.ALWAYS : ScoreboardTeam.NameTagVisibility.NEVER;
			scoreboardTeam.setNameTagVisibility(visibility);
		}

		@Override
		public ScoreboardTeam getScoreboardTeam(Team team) {
			return scoreboardTeam;
		}

		@Override
		public void update() {
			match.getMinigamers().forEach(minigamer -> {
				if (!minigamer.isAlive())
					scoreboardTeam.removePlayer(minigamer.getPlayer());
				else
					scoreboardTeam.addPlayer(minigamer.getPlayer());
			});
			PlayerUtils.getOnlinePlayers().forEach(scoreboardTeam::subscribe);
		}

		@Override
		public void handleJoin(Minigamer minigamer) {
			scoreboardTeam.addPlayer(minigamer.getPlayer());
			PlayerUtils.getOnlinePlayers().forEach(scoreboardTeam::subscribe);
		}

		@Override
		public void handleQuit(Minigamer minigamer) {
			scoreboardTeam.removePlayer(minigamer.getPlayer());
			PlayerUtils.getOnlinePlayers().forEach(scoreboardTeam::subscribe);
		}

		@Override
		public void handleEnd() {
			PlayerUtils.getOnlinePlayers().forEach(player -> {
				scoreboardTeam.removePlayer(player);
				scoreboardTeam.unsubscribe(player);
			});
			Minigames.getScoreboard().removeTeam(scoreboardTeam.getId());
		}
	}

}
