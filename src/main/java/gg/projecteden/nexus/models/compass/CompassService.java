package gg.projecteden.nexus.models.compass;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Compass.class)
public class CompassService extends MongoService<Compass> {
	private final static Map<UUID, Compass> cache = new ConcurrentHashMap<>();

	public Map<UUID, Compass> getCache() {
		return cache;
	}

}
