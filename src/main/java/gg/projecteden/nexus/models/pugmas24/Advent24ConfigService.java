package gg.projecteden.nexus.models.pugmas24;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Advent24Config.class)
public class Advent24ConfigService extends MongoPlayerService<Advent24Config> {
	private final static Map<UUID, Advent24Config> cache = new ConcurrentHashMap<>();

	public Map<UUID, Advent24Config> getCache() {
		return cache;
	}

}

