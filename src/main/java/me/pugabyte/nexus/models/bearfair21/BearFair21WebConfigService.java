package me.pugabyte.nexus.models.bearfair21;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(BearFair21WebConfig.class)
public class BearFair21WebConfigService extends MongoService<BearFair21WebConfig> {
	private final static Map<UUID, BearFair21WebConfig> cache = new HashMap<>();

	public Map<UUID, BearFair21WebConfig> getCache() {
		return cache;
	}
}
