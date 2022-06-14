package gg.projecteden.nexus.models.witherarena;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(WitherArenaConfig.class)
public class WitherArenaConfigService extends MongoPlayerService<WitherArenaConfig> {
	private final static Map<UUID, WitherArenaConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, WitherArenaConfig> getCache() {
		return cache;
	}

}
