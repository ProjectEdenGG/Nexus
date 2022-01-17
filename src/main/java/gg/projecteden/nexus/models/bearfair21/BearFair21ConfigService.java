package gg.projecteden.nexus.models.bearfair21;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(BearFair21Config.class)
public class BearFair21ConfigService extends MongoPlayerService<BearFair21Config> {
	private final static Map<UUID, BearFair21Config> cache = new ConcurrentHashMap<>();

	public Map<UUID, BearFair21Config> getCache() {
		return cache;
	}
}
