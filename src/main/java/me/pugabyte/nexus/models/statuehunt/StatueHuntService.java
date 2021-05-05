package me.pugabyte.nexus.models.statuehunt;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(StatueHunt.class)
public class StatueHuntService extends MongoService<StatueHunt> {
	private final static Map<UUID, StatueHunt> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, StatueHunt> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}
}
