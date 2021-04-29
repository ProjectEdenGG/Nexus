package me.pugabyte.nexus.models.pvp;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(PVP.class)
public class PVPService extends MongoService<PVP> {
	private final static Map<UUID, PVP> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	@Override
	public Map<UUID, PVP> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}
}
