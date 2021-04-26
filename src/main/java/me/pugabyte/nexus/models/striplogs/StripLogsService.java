package me.pugabyte.nexus.models.striplogs;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(StripLogs.class)
public class StripLogsService extends MongoService<StripLogs> {
	private final static Map<UUID, StripLogs> cache = new HashMap<>();

	public Map<UUID, StripLogs> getCache() {
		return cache;
	}

}
