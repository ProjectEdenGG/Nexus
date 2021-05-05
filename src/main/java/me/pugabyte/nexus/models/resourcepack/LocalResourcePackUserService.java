package me.pugabyte.nexus.models.resourcepack;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(LocalResourcePackUser.class)
public class LocalResourcePackUserService extends MongoService<LocalResourcePackUser> {
	private final static Map<UUID, LocalResourcePackUser> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, LocalResourcePackUser> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
