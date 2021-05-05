package me.pugabyte.nexus.models.queup;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(QueUp.class)
public class QueUpService extends MongoService<QueUp> {
	private final static Map<UUID, QueUp> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, QueUp> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public QueUp get() {
		return get(Nexus.getUUID0());
	}

}
