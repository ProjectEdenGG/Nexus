package me.pugabyte.bncore.models.bearfair;

import dev.morphia.query.Sort;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BearFairService extends MongoService {
	private final static Map<UUID, BearFairUser> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public BearFairUser get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			BearFairUser user = database.createQuery(BearFairUser.class).field(_id).equal(uuid).first();
			if (user == null)
				user = new BearFairUser(uuid);
			return user;
		});

		return cache.get(uuid);
	}

	public List<BearFairUser> getTopPoints(int page) {
		return database.createQuery(BearFairUser.class)
				.order(Sort.descending("totalPoints"))
				.limit(10)
				.offset((page - 1) * 10)
				.find().toList();
	}

}
