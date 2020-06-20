package me.pugabyte.bncore.models.achievement;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AchievementPlayer.class)
public class AchievementService extends MongoService {
	private final static Map<UUID, AchievementPlayer> cache = new HashMap<>();

	public Map<UUID, AchievementPlayer> getCache() {
		return cache;
	}

}
