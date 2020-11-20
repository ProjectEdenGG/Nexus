package me.pugabyte.nexus.models.shop;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.utils.Tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Shop.class)
public class ShopService extends MongoService {
	private final static Map<UUID, Shop> cache = new HashMap<>();

	public Map<UUID, Shop> getCache() {
		return cache;
	}

	static {
		Tasks.async(() -> database.createQuery(Shop.class).find().forEachRemaining(shop -> cache.put(shop.getUuid(), shop)));
	}

	public List<Shop> getShops() {
		return new ArrayList<>(cache.values());
	}

	public Shop getMarket() {
		return get(Nexus.getUUID0());
	}

}
