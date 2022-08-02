package gg.projecteden.nexus.models.eventuser;

import dev.morphia.query.Sort;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(EventUser.class)
public class EventUserService extends MongoPlayerService<EventUser> {
	private final static Map<UUID, EventUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, EventUser> getCache() {
		return cache;
	}

	public List<EventUser> getTopTokens() {
		return getAllSortedBy(Sort.descending("tokens"));
	}

}
