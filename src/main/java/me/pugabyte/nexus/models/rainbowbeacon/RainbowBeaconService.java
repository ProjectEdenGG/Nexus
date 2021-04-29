package me.pugabyte.nexus.models.rainbowbeacon;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(RainbowBeacon.class)
public class RainbowBeaconService extends MongoService<RainbowBeacon> {
	private final static Map<UUID, RainbowBeacon> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, RainbowBeacon> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
