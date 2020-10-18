package me.pugabyte.bncore.models.halloween20;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Halloween20User.class)
public class Halloween20Service extends MongoService {

	public static Map<UUID, Halloween20User> cache = new HashMap<>();

	@Override
	public Map<UUID, Halloween20User> getCache() {
		return cache;
	}
}
