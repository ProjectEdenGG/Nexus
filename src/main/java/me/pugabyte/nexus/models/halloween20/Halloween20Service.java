package me.pugabyte.nexus.models.halloween20;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Halloween20User.class)
public class Halloween20Service extends MongoService<Halloween20User> {

	public static Map<UUID, Halloween20User> cache = new HashMap<>();

	@Override
	public Map<UUID, Halloween20User> getCache() {
		return cache;
	}
}
