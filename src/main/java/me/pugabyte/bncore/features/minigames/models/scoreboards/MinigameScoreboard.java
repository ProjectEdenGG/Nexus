package me.pugabyte.bncore.features.minigames.models.scoreboards;

import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;

public interface MinigameScoreboard {

	void update();

	void handleJoin(Minigamer minigamer);

	void handleQuit(Minigamer minigamer);

	void handleEnd();

	class Factory {
		public static MinigameScoreboard create(Match match) {
			if (!match.getArena().hasScoreboard())
				return null;

			if (match.getArena().hasUniqueScoreboards())
				return new MinigamerScoreboard(match);

			return new MatchScoreboard(match);
		}
	}

}
