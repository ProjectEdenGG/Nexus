package gg.projecteden.nexus.models.boost;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(BoostConfig.class)
public class BoostConfigService extends MongoService<BoostConfig> {
	private final static Map<UUID, BoostConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, BoostConfig> getCache() {
		return cache;
	}

}
