package me.pugabyte.bncore.models.safecracker;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SafeCrackerPlayer.class)
public class SafeCrackerPlayerService extends MongoService {
	private final static Map<UUID, SafeCrackerPlayer> cache = new HashMap<>();

	public Map<UUID, SafeCrackerPlayer> getCache() {
		return cache;
	}

}
