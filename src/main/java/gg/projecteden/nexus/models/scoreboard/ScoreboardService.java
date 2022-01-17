package gg.projecteden.nexus.models.scoreboard;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ScoreboardUser.class)
public class ScoreboardService extends MongoPlayerService<ScoreboardUser> {
	private final static Map<UUID, ScoreboardUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, ScoreboardUser> getCache() {
		return cache;
	}

	@Override
	protected void beforeDelete(ScoreboardUser user) {
		if (user.getScoreboard() != null)
			user.getScoreboard().delete();
	}

}
