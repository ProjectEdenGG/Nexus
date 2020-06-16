package me.pugabyte.bncore.models.rainbowbeacon;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(RainbowBeacon.class)
public class RainbowBeaconService extends MongoService {
	private final static Map<UUID, RainbowBeacon> cache = new HashMap<>();

	public Map<UUID, RainbowBeacon> getCache() {
		return cache;
	}

}
