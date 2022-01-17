package gg.projecteden.nexus.models.rainbowbeacon;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(RainbowBeacon.class)
public class RainbowBeaconService extends MongoPlayerService<RainbowBeacon> {
	private final static Map<UUID, RainbowBeacon> cache = new ConcurrentHashMap<>();

	public Map<UUID, RainbowBeacon> getCache() {
		return cache;
	}

}
