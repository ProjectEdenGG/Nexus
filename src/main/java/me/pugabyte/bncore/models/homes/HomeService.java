package me.pugabyte.bncore.models.homes;

import dev.morphia.query.UpdateException;
import me.pugabyte.bncore.BNCore;
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
		Tasks.async(() -> {
			try {
				datastore.merge(homeOwner);
			} catch (UpdateException ex) {
				try {
					datastore.save(homeOwner);
				} catch (Exception ex2) {
					BNCore.log("Error saving HomeOwner " + homeOwner.getOfflinePlayer().getName());
					ex2.printStackTrace();
				}
			} catch (Exception ex3) {
				BNCore.log("Error updating HomeOwner " + homeOwner.getOfflinePlayer().getName());
				ex3.printStackTrace();
			}
		});
	}

}
