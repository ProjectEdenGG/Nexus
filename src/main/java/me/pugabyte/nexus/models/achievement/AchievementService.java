package me.pugabyte.nexus.models.achievement;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AchievementPlayer.class)
public class AchievementService extends MongoService<AchievementPlayer> {
	private final static Map<UUID, AchievementPlayer> cache = new HashMap<>();

	public Map<UUID, AchievementPlayer> getCache() {
		return cache;
	}

}
