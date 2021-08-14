package gg.projecteden.nexus.models.newrankcolors;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(NewRankColors.class)
public class NewRankColorsService extends MongoService<NewRankColors> {
	private final static Map<UUID, NewRankColors> cache = new ConcurrentHashMap<>();

	public Map<UUID, NewRankColors> getCache() {
		return cache;
	}

}
