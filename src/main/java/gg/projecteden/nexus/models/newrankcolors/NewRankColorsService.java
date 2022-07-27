package gg.projecteden.nexus.models.newrankcolors;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(NewRankColors.class)
public class NewRankColorsService extends MongoPlayerService<NewRankColors> {
	private final static Map<UUID, NewRankColors> cache = new ConcurrentHashMap<>();

	public Map<UUID, NewRankColors> getCache() {
		return cache;
	}

}
