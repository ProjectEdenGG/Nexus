package me.pugabyte.bncore.models.homes;

import me.pugabyte.bncore.models.MongoService;

import java.util.Comparator;
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
		cache.computeIfAbsent(uuid, $ -> {
			HomeOwner homeOwner = database.createQuery(HomeOwner.class).field(_id).equal(uuid).first();
			if (homeOwner == null)
				homeOwner = new HomeOwner(uuid);
			return homeOwner;
		});

		return cache.get(uuid);
	}

	@Override
	public <T> void save(T object) {
		((HomeOwner) object).getHomes().sort(Comparator.comparing(home -> home.getName().toLowerCase()));
		super.save(object);
	}

}
