package me.pugabyte.bncore.features.minigames.models.scoreboards;

import lombok.Getter;
import lombok.SneakyThrows;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.Scoreboard;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;

public interface MinigameScoreboard {

	void update();

	void handleJoin(Minigamer minigamer);

	void handleQuit(Minigamer minigamer);

	void handleEnd();


	class Factory {

		@SneakyThrows
		public static MinigameScoreboard create(Match match) {
			Mechanic mechanic = match.getArena().getMechanic();
			Scoreboard annotation = mechanic.getAnnotation(Scoreboard.class);
			Class<? extends MinigameScoreboard> type = Type.MATCH.getType();
			if (annotation != null)
				type = annotation.value().getType();

			if (type != null)
				return type.getDeclaredConstructor(Match.class).newInstance(match);

			return null;
		}

	}

	enum Type {
		NONE(null),
		MATCH(MatchScoreboard.class),
		MINIGAMER(MinigamerScoreboard.class);

		@Getter
		private Class<? extends MinigameScoreboard> type;

		Type(Class<? extends MinigameScoreboard> clazz) {
			this.type = clazz;
		}
	}

}
