package me.pugabyte.bncore.models.shop;

import me.pugabyte.bncore.models.MongoService;
import me.pugabyte.bncore.utils.Tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopService extends MongoService {
	private final static Map<UUID, Shop> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	static {
		Tasks.async(() -> database.createQuery(Shop.class).find().forEachRemaining(shop -> cache.put(shop.getUuid(), shop)));
	}

	public List<Shop> getShops() {
		return new ArrayList<>(cache.values());
	}

	@Override
	public Shop get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			Shop shop = database.createQuery(Shop.class).field(_id).equal(uuid).first();
			if (shop == null)
				shop = new Shop(uuid);
			return shop;
		});

		return cache.get(uuid);
	}

}
