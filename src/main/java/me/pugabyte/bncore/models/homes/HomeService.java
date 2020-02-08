package me.pugabyte.bncore.models.homes;

import dev.morphia.query.UpdateException;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.BaseService;
import me.pugabyte.bncore.utils.Tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

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
			Supplier<String> name = () -> homeOwner.getOfflinePlayer() == null ? homeOwner.getUuid().toString() : homeOwner.getOfflinePlayer().getName();
			try {
				datastore.merge(homeOwner);
			} catch (UpdateException doesntExistYet) {
				try {
					datastore.save(homeOwner);
				} catch (Exception ex2) {
					BNCore.log("Error saving HomeOwner " + name.get());
					ex2.printStackTrace();
				}
			} catch (Exception ex3) {
				BNCore.log("Error updating HomeOwner " + name.get());
				ex3.printStackTrace();
			}
		});
	}

}
