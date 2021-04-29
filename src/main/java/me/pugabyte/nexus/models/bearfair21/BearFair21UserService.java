package me.pugabyte.nexus.models.bearfair21;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(BearFair21User.class)
public class BearFair21UserService extends MongoService<BearFair21User> {
	private final static Map<UUID, BearFair21User> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, BearFair21User> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}
}
