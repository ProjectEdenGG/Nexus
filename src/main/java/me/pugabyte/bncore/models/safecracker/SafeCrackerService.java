package me.pugabyte.bncore.models.safecracker;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SafeCracker.class)
public class SafeCrackerService extends MongoService {
	private final static Map<UUID, SafeCracker> cache = new HashMap<>();

	public Map<UUID, SafeCracker> getCache() {
		return cache;
	}

}
