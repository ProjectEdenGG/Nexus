package me.pugabyte.bncore.models.homes;

import me.pugabyte.bncore.models.BaseService;
import me.pugabyte.bncore.utils.Tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeService extends BaseService {
	private final static Map<UUID, HomeOwner> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public HomeOwner get(UUID uuid) {
		if (!cache.containsKey(uuid)) {
			HomeOwner homeOwner = datastore.createQuery(HomeOwner.class).field("_id").equal(uuid).first();
			if (homeOwner == null)
				homeOwner = new HomeOwner(uuid);
			cache.put(uuid, homeOwner);
		}

		return cache.get(uuid);
	}

	public void save(HomeOwner homeOwner) {
		Tasks.async(() -> datastore.merge(homeOwner));
	}

}
