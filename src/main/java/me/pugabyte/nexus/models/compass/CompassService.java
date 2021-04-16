package me.pugabyte.nexus.models.compass;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Compass.class)
public class CompassService extends MongoService<Compass> {
	private final static Map<UUID, Compass> cache = new HashMap<>();

	public Map<UUID, Compass> getCache() {
		return cache;
	}

}
