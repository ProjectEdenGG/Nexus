package me.pugabyte.nexus.models.eventuser;

import dev.morphia.query.Sort;
import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(EventUser.class)
public class EventUserService extends MongoService<EventUser> {
	private final static Map<UUID, EventUser> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, EventUser> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public List<EventUser> getTopTokens() {
		return getAllSortedBy(Sort.descending("tokens"));
	}

}
