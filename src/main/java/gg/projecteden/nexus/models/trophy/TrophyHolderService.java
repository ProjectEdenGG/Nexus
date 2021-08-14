package gg.projecteden.nexus.models.trophy;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(TrophyHolder.class)
public class TrophyHolderService extends MongoService<TrophyHolder> {
	private final static Map<UUID, TrophyHolder> cache = new ConcurrentHashMap<>();

	public Map<UUID, TrophyHolder> getCache() {
		return cache;
	}

}
