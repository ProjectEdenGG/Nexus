package me.pugabyte.nexus.models.boost;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(BoostConfig.class)
public class BoostConfigService extends MongoService<BoostConfig> {
	private final static Map<UUID, BoostConfig> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, BoostConfig> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public BoostConfig get() {
		return get(Nexus.getUUID0());
	}

}
