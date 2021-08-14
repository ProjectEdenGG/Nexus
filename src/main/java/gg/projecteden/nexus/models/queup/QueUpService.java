package gg.projecteden.nexus.models.queup;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(QueUp.class)
public class QueUpService extends MongoService<QueUp> {
	private final static Map<UUID, QueUp> cache = new ConcurrentHashMap<>();

	public Map<UUID, QueUp> getCache() {
		return cache;
	}

}
