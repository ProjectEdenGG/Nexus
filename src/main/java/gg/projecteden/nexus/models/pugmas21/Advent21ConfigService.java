package gg.projecteden.nexus.models.pugmas21;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Advent21Config.class)
public class Advent21ConfigService extends MongoService<Advent21Config> {
	private final static Map<UUID, Advent21Config> cache = new ConcurrentHashMap<>();

	public Map<UUID, Advent21Config> getCache() {
		return cache;
	}

}
