package me.pugabyte.nexus.models.queup;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(QueUp.class)
public class QueUpService extends MongoService<QueUp> {
	private final static Map<UUID, QueUp> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

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
