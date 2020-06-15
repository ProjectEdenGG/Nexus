package me.pugabyte.bncore.models.compass;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Compass.class)
public class CompassService extends MongoService {
	private final static Map<UUID, Compass> cache = new HashMap<>();

	public Map<UUID, Compass> getCache() {
		return cache;
	}

}
