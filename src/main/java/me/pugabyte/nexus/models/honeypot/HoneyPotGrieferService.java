package me.pugabyte.nexus.models.honeypot;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(HoneyPotGriefer.class)
public class HoneyPotGrieferService extends MongoService<HoneyPotGriefer> {
	private final static Map<UUID, HoneyPotGriefer> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, HoneyPotGriefer> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
