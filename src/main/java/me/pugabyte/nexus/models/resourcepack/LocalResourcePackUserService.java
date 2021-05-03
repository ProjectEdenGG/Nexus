package me.pugabyte.nexus.models.resourcepack;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(LocalResourcePackUser.class)
public class LocalResourcePackUserService extends MongoService<LocalResourcePackUser> {
	private final static Map<UUID, LocalResourcePackUser> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, LocalResourcePackUser> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
