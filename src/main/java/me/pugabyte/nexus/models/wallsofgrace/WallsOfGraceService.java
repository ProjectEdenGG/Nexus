package me.pugabyte.nexus.models.wallsofgrace;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(WallsOfGrace.class)
public class WallsOfGraceService extends MongoService<WallsOfGrace> {
	private final static Map<UUID, WallsOfGrace> cache = new HashMap<>();

	public Map<UUID, WallsOfGrace> getCache() {
		return cache;
	}

}
