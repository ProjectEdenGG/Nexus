package me.pugabyte.nexus.models.skincache;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SkinCache.class)
public class SkinCacheService extends MongoService<SkinCache> {
	private final static Map<UUID, SkinCache> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, SkinCache> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
