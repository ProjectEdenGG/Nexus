package me.pugabyte.nexus.models.rainbowbeacon;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(RainbowBeacon.class)
public class RainbowBeaconService extends MongoService<RainbowBeacon> {
	private final static Map<UUID, RainbowBeacon> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, RainbowBeacon> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
