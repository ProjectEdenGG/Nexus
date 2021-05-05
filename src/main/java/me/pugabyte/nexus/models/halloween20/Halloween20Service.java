package me.pugabyte.nexus.models.halloween20;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Halloween20User.class)
public class Halloween20Service extends MongoService<Halloween20User> {
	private final static Map<UUID, Halloween20User> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, Halloween20User> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}
}
