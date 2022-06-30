package gg.projecteden.nexus.models.striplogs;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(StripLogs.class)
public class StripLogsService extends MongoPlayerService<StripLogs> {
	private final static Map<UUID, StripLogs> cache = new ConcurrentHashMap<>();

	public Map<UUID, StripLogs> getCache() {
		return cache;
	}

}
