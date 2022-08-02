package gg.projecteden.nexus.models.pugmas20;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Pugmas20User.class)
public class Pugmas20UserService extends MongoPlayerService<Pugmas20User> {
	private final static Map<UUID, Pugmas20User> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, Pugmas20User> getCache() {
		return cache;
	}

}
