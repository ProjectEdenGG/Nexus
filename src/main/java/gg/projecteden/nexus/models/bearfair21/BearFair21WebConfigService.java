package gg.projecteden.nexus.models.bearfair21;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(BearFair21WebConfig.class)
public class BearFair21WebConfigService extends MongoService<BearFair21WebConfig> {
	private final static Map<UUID, BearFair21WebConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, BearFair21WebConfig> getCache() {
		return cache;
	}

}
