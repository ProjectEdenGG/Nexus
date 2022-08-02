package gg.projecteden.nexus.models.legacy.shops;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(LegacyShop.class)
public class LegacyShopService extends MongoPlayerService<LegacyShop> {
	private final static Map<UUID, LegacyShop> cache = new ConcurrentHashMap<>();

	public Map<UUID, LegacyShop> getCache() {
		return cache;
	}

}
