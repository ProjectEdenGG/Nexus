package me.pugabyte.bncore.models.shop;

import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopService extends MongoService {
	public final static UUID MARKET = new UUID(0, 0);
	private final static Map<UUID, Shop> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
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
