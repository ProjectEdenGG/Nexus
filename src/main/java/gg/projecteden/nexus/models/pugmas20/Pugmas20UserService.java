package gg.projecteden.nexus.models.pugmas20;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Pugmas20User.class)
public class Pugmas20UserService extends MongoService<Pugmas20User> {
	private final static Map<UUID, Pugmas20User> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, Pugmas20User> getCache() {
		return cache;
	}

}
