package me.pugabyte.nexus.models.bearfair;

import dev.morphia.query.Sort;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(BearFairUser.class)
public class BearFairService extends MongoService {
	private final static Map<UUID, BearFairUser> cache = new HashMap<>();

	public Map<UUID, BearFairUser> getCache() {
		return cache;
	}

	public List<BearFairUser> getTopPoints(int page) {
		return database.createQuery(BearFairUser.class)
				.order(Sort.descending("totalPoints"))
				.limit(10)
				.offset((page - 1) * 10)
				.find().toList();
	}

}
