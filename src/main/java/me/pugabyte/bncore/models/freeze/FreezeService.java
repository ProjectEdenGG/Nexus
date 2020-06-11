package me.pugabyte.bncore.models.freeze;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Freeze.class)
public class FreezeService extends MongoService {
	private final static Map<UUID, Freeze> cache = new HashMap<>();

	public Map<UUID, Freeze> getCache() {
		return cache;
	}

}
