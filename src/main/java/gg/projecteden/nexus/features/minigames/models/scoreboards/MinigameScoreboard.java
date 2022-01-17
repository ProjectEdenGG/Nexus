package gg.projecteden.nexus.features.minigames.models.scoreboards;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.Scoreboard;
import lombok.Getter;
import lombok.SneakyThrows;

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

}
