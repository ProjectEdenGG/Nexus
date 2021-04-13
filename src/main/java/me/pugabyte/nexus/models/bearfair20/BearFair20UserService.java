package me.pugabyte.nexus.models.bearfair20;

import dev.morphia.query.Sort;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(BearFair20User.class)
public class BearFair20UserService extends MongoService {
	private final static Map<UUID, BearFair20User> cache = new HashMap<>();

	public Map<UUID, BearFair20User> getCache() {
		return cache;
	}

	public List<BearFair20User> getTopPoints(int page) {
		return database.createQuery(BearFair20User.class)
				.order(Sort.descending("totalPoints"))
				.limit(10)
				.offset((page - 1) * 10)
				.find().toList();
	}

}
