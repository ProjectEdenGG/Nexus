package me.pugabyte.bncore.models.wallsofgrace;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(WallsOfGrace.class)
public class WallsOfGraceService extends MongoService {
	private final static Map<UUID, WallsOfGrace> cache = new HashMap<>();

	public Map<UUID, WallsOfGrace> getCache() {
		return cache;
	}

}
