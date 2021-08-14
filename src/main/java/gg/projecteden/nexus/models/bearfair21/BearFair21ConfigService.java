package gg.projecteden.nexus.models.bearfair21;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(BearFair21Config.class)
public class BearFair21ConfigService extends MongoService<BearFair21Config> {
	private final static Map<UUID, BearFair21Config> cache = new ConcurrentHashMap<>();

	public Map<UUID, BearFair21Config> getCache() {
		return cache;
	}
}
