package me.pugabyte.nexus.models.achievement;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AchievementPlayer.class)
public class AchievementService extends MongoService<AchievementPlayer> {
	private final static Map<UUID, AchievementPlayer> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, AchievementPlayer> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
