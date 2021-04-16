package me.pugabyte.nexus.models.scoreboard;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(ScoreboardUser.class)
public class ScoreboardService extends MongoService<ScoreboardUser> {
	private final static Map<UUID, ScoreboardUser> cache = new HashMap<>();

	public Map<UUID, ScoreboardUser> getCache() {
		return cache;
	}

	public void delete(ScoreboardUser user) {
		if (user.getScoreboard() != null)
			user.getScoreboard().delete();
		super.delete(user);
	}

}
