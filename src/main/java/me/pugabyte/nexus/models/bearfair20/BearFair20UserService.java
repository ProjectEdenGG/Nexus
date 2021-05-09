package me.pugabyte.nexus.models.bearfair20;

import dev.morphia.query.Sort;
import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(BearFair20User.class)
public class BearFair20UserService extends MongoService<BearFair20User> {
	private final static Map<UUID, BearFair20User> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, BearFair20User> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public List<BearFair20User> getTopPoints(int page) {
		return database.createQuery(BearFair20User.class)
				.order(Sort.descending("totalPoints"))
				.limit(10)
				.offset((page - 1) * 10)
				.find().toList();
	}

}
