package gg.projecteden.nexus.models.curiosity;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Curiosity.class)
public class CuriosityService extends MongoService<Curiosity> {
	private final static Map<UUID, Curiosity> cache = new ConcurrentHashMap<>();

	public Map<UUID, Curiosity> getCache() {
		return cache;
	}

}
