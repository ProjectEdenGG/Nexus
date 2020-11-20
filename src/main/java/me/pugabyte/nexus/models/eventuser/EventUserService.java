package me.pugabyte.nexus.models.eventuser;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(EventUser.class)
public class EventUserService extends MongoService {
	private final static Map<UUID, EventUser> cache = new HashMap<>();

	public Map<UUID, EventUser> getCache() {
		return cache;
	}

}
