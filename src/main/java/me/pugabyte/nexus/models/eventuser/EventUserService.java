package me.pugabyte.nexus.models.eventuser;

import dev.morphia.query.Sort;
import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(EventUser.class)
public class EventUserService extends MongoService<EventUser> {
	private final static Map<UUID, EventUser> cache = new HashMap<>();

	public Map<UUID, EventUser> getCache() {
		return cache;
	}

	public List<EventUser> getTopTokens() {
		return getAllSortedBy(Sort.descending("tokens"));
	}

}
