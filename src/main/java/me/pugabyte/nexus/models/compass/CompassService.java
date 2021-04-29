package me.pugabyte.nexus.models.compass;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Compass.class)
public class CompassService extends MongoService<Compass> {
	private final static Map<UUID, Compass> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, Compass> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
