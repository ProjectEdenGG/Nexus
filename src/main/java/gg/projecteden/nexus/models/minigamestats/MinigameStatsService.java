package gg.projecteden.nexus.models.minigamestats;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(MinigameStatsUser.class)
public class MinigameStatsService extends MongoPlayerService<MinigameStatsUser> {
	private final static Map<UUID, MinigameStatsUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, MinigameStatsUser> getCache() {
		return cache;
	}

}
