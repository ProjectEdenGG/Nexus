package gg.projecteden.nexus.models.pugmas21;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Advent21Config.class)
public class Advent21ConfigService extends MongoPlayerService<Advent21Config> {
	private final static Map<UUID, Advent21Config> cache = new ConcurrentHashMap<>();

	public Map<UUID, Advent21Config> getCache() {
		return cache;
	}

}
