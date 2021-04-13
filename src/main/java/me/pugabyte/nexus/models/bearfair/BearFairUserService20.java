package me.pugabyte.nexus.models.bearfair;

import dev.morphia.query.Sort;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(BearFairUser20.class)
public class BearFairUserService20 extends MongoService {
	private final static Map<UUID, BearFairUser20> cache = new HashMap<>();

	public Map<UUID, BearFairUser20> getCache() {
		return cache;
	}

	public List<BearFairUser20> getTopPoints(int page) {
		return database.createQuery(BearFairUser20.class)
				.order(Sort.descending("totalPoints"))
				.limit(10)
				.offset((page - 1) * 10)
				.find().toList();
	}

}
