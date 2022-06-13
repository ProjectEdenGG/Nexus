package gg.projecteden.nexus.models.trophy;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(TrophyHolder.class)
public class TrophyHolderService extends MongoPlayerService<TrophyHolder> {
	private final static Map<UUID, TrophyHolder> cache = new ConcurrentHashMap<>();

	public Map<UUID, TrophyHolder> getCache() {
		return cache;
	}

}
