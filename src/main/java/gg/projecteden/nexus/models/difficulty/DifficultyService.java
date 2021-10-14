package gg.projecteden.nexus.models.difficulty;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(DifficultyUser.class)
public class DifficultyService extends MongoService<DifficultyUser> {
	private final static Map<UUID, DifficultyUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, DifficultyUser> getCache() {
		return cache;
	}
}
