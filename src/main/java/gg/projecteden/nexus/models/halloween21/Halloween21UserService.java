package gg.projecteden.nexus.models.halloween21;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Halloween21User.class)
public class Halloween21UserService extends MongoService<Halloween21User> {
	private final static Map<UUID, Halloween21User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Halloween21User> getCache() {
		return cache;
	}

}
