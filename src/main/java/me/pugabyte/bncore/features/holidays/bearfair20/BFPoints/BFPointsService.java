package me.pugabyte.bncore.features.holidays.bearfair20.BFPoints;

import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BFPointsService extends MongoService {
	private final static Map<UUID, BFPointsUser> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public BFPointsUser get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			BFPointsUser user = database.createQuery(BFPointsUser.class).field(_id).equal(uuid).first();
			if (user == null)
				user = new BFPointsUser(uuid);
			return user;
		});

		return cache.get(uuid);
	}

}
