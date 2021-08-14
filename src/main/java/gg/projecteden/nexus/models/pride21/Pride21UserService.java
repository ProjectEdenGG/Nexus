package gg.projecteden.nexus.models.pride21;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Pride21User.class)
public class Pride21UserService extends MongoService<Pride21User> {
	private static final Map<UUID, Pride21User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Pride21User> getCache() {
		return cache;
	}

}
