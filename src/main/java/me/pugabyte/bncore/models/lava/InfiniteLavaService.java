package me.pugabyte.bncore.models.lava;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(InfiniteLava.class)
public class InfiniteLavaService extends MongoService {
	private final static Map<UUID, InfiniteLava> cache = new HashMap<>();

	public Map<UUID, InfiniteLava> getCache() {
		return cache;
	}

}
