package gg.projecteden.nexus.models.easter21;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Easter21User.class)
public class Easter21UserService extends MongoService<Easter21User> {
	private final static Map<UUID, Easter21User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Easter21User> getCache() {
		return cache;
	}

}
