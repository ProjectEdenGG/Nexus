package me.pugabyte.nexus.models.wallsofgrace;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(WallsOfGrace.class)
public class WallsOfGraceService extends MongoService<WallsOfGrace> {
	private final static Map<UUID, WallsOfGrace> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, WallsOfGrace> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	protected boolean deleteIf(WallsOfGrace wallsOfGrace) {
		return wallsOfGrace.get(1) == null && wallsOfGrace.get(2) == null;
	}

}
