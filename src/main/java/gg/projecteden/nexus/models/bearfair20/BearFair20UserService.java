package gg.projecteden.nexus.models.bearfair20;

import dev.morphia.query.Sort;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(BearFair20User.class)
public class BearFair20UserService extends MongoPlayerService<BearFair20User> {
	private final static Map<UUID, BearFair20User> cache = new ConcurrentHashMap<>();

	public Map<UUID, BearFair20User> getCache() {
		return cache;
	}

	public List<BearFair20User> getTopPoints(int page) {
		final var query = database.createQuery(BearFair20User.class)
			.order(Sort.descending("totalPoints"))
			.limit(10)
			.offset((page - 1) * 10);

		try (var cursor = query.find()) {
			return cursor.toList();
		}
	}

}
