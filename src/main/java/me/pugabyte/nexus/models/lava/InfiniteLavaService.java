package me.pugabyte.nexus.models.lava;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(InfiniteLava.class)
public class InfiniteLavaService extends MongoService<InfiniteLava> {
	private final static Map<UUID, InfiniteLava> cache = new HashMap<>();

	public Map<UUID, InfiniteLava> getCache() {
		return cache;
	}

}
