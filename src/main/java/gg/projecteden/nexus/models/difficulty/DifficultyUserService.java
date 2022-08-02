package gg.projecteden.nexus.models.difficulty;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DifficultyUser.class)
public class DifficultyUserService extends MongoPlayerService<DifficultyUser> {
	private final static Map<UUID, DifficultyUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, DifficultyUser> getCache() {
		return cache;
	}
}
