package gg.projecteden.nexus.models.striplogs;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(StripLogs.class)
public class StripLogsService extends MongoService<StripLogs> {
	private final static Map<UUID, StripLogs> cache = new ConcurrentHashMap<>();

	public Map<UUID, StripLogs> getCache() {
		return cache;
	}

}
