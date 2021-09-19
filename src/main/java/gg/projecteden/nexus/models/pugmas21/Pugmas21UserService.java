package gg.projecteden.nexus.models.pugmas21;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Pugmas21User.class)
public class Pugmas21UserService extends MongoService<Pugmas21User> {
	private final static Map<UUID, Pugmas21User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Pugmas21User> getCache() {
		return cache;
	}

}
