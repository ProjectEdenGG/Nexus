package gg.projecteden.nexus.models.difficulty;

import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.api.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DifficultyUser.class)
public class DifficultyService extends MongoService<DifficultyUser> {
	private final static Map<UUID, DifficultyUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, DifficultyUser> getCache() {
		return cache;
	}
}
