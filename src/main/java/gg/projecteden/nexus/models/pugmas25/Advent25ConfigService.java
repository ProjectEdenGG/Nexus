package gg.projecteden.nexus.models.pugmas25;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Advent25Config.class)
public class Advent25ConfigService extends MongoPlayerService<Advent25Config> {
	private final static Map<UUID, Advent25Config> cache = new ConcurrentHashMap<>();

	public Map<UUID, Advent25Config> getCache() {
		return cache;
	}

}

