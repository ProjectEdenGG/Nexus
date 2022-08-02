package gg.projecteden.nexus.models.bearfair21;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(BearFair21WebConfig.class)
public class BearFair21WebConfigService extends MongoPlayerService<BearFair21WebConfig> {
	private final static Map<UUID, BearFair21WebConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, BearFair21WebConfig> getCache() {
		return cache;
	}

}
