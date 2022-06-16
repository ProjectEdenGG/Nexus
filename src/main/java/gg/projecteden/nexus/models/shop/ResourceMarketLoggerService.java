package gg.projecteden.nexus.models.shop;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ResourceMarketLogger.class)
public class ResourceMarketLoggerService extends MongoPlayerService<ResourceMarketLogger> {
	private final static Map<UUID, ResourceMarketLogger> cache = new ConcurrentHashMap<>();

	public Map<UUID, ResourceMarketLogger> getCache() {
		return cache;
	}

}
