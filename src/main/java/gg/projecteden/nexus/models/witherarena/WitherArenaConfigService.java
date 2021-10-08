package gg.projecteden.nexus.models.witherarena;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(WitherArenaConfig.class)
public class WitherArenaConfigService extends MongoService<WitherArenaConfig> {
	private final static Map<UUID, WitherArenaConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, WitherArenaConfig> getCache() {
		return cache;
	}

}
