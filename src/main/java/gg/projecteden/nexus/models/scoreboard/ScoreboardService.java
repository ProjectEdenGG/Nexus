package gg.projecteden.nexus.models.scoreboard;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

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
		user.getLayout().stop();
	}

	@Override
	protected void beforeSave(ScoreboardUser user) {
		user.flushScoreboard();
	}
}
