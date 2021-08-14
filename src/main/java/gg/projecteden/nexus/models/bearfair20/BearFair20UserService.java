package gg.projecteden.nexus.models.bearfair20;

import dev.morphia.query.Sort;
import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(BearFair20User.class)
public class BearFair20UserService extends MongoService<BearFair20User> {
	private final static Map<UUID, BearFair20User> cache = new ConcurrentHashMap<>();

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
