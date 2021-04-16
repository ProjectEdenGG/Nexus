package me.pugabyte.nexus.models.pugmas20;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Pugmas20User.class)
public class Pugmas20UserService extends MongoService<Pugmas20User> {

	public static Map<UUID, Pugmas20User> cache = new HashMap<>();

	@Override
	public Map<UUID, Pugmas20User> getCache() {
		return cache;
	}

}
