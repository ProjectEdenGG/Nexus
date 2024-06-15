package gg.projecteden.nexus.models.pugmas24;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Pugmas24User.class)
public class Pugmas24UserService extends MongoPlayerService<Pugmas24User> {
	private final static Map<UUID, Pugmas24User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Pugmas24User> getCache() {
		return cache;
	}

}
