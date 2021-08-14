package gg.projecteden.nexus.models.honeypot;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(HoneyPotGriefer.class)
public class HoneyPotGrieferService extends MongoService<HoneyPotGriefer> {
	private final static Map<UUID, HoneyPotGriefer> cache = new ConcurrentHashMap<>();

	public Map<UUID, HoneyPotGriefer> getCache() {
		return cache;
	}

}
