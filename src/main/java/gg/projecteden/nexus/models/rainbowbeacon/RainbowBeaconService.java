package gg.projecteden.nexus.models.rainbowbeacon;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(RainbowBeacon.class)
public class RainbowBeaconService extends MongoService<RainbowBeacon> {
	private final static Map<UUID, RainbowBeacon> cache = new ConcurrentHashMap<>();

	public Map<UUID, RainbowBeacon> getCache() {
		return cache;
	}

}
