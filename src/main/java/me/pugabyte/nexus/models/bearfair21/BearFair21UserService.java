package me.pugabyte.nexus.models.bearfair21;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(BearFair21User.class)
public class BearFair21UserService extends MongoService<BearFair21User> {
	private final static Map<UUID, BearFair21User> cache = new HashMap<>();

	public Map<UUID, BearFair21User> getCache() {
		return cache;
	}
}
