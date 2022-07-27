package gg.projecteden.nexus.models.boost;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(BoostConfig.class)
public class BoostConfigService extends MongoPlayerService<BoostConfig> {
	private final static Map<UUID, BoostConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, BoostConfig> getCache() {
		return cache;
	}

}
