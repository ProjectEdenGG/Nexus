package me.pugabyte.nexus.models.freeze;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Freeze.class)
public class FreezeService extends MongoService<Freeze> {
	private final static Map<UUID, Freeze> cache = new HashMap<>();

	public Map<UUID, Freeze> getCache() {
		return cache;
	}

}
