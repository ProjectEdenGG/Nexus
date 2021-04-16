package me.pugabyte.nexus.models.rainbowbeacon;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(RainbowBeacon.class)
public class RainbowBeaconService extends MongoService<RainbowBeacon> {
	private final static Map<UUID, RainbowBeacon> cache = new HashMap<>();

	public Map<UUID, RainbowBeacon> getCache() {
		return cache;
	}

}
