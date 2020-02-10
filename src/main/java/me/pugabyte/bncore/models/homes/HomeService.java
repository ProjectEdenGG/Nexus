package me.pugabyte.bncore.models.homes;

import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeService extends MongoService {
	private final static Map<UUID, HomeOwner> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public HomeOwner get(UUID uuid) {
		if (!cache.containsKey(uuid)) {
			HomeOwner homeOwner = database.createQuery(HomeOwner.class).field("_id").equal(uuid).first();
			if (homeOwner == null)
				homeOwner = new HomeOwner(uuid);
			cache.put(uuid, homeOwner);
		}

		return cache.get(uuid);
	}

}
