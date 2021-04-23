package me.pugabyte.nexus.models.watchlist;

import dev.morphia.query.Sort;
import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Watchlisted.class)
public class WatchlistedService extends MongoService<Watchlisted> {
	private final static Map<UUID, Watchlisted> cache = new HashMap<>();

	public Map<UUID, Watchlisted> getCache() {
		return cache;
	}

	public List<Watchlisted> getAllActive() {
		return database.createQuery(getPlayerClass())
				.filter("active", true)
				.order(Sort.descending("watchlistedOn"))
				.find().toList();
	}

}
