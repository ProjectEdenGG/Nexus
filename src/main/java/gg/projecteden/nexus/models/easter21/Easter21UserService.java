package gg.projecteden.nexus.models.easter21;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Easter21User.class)
public class Easter21UserService extends MongoPlayerService<Easter21User> {
	private final static Map<UUID, Easter21User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Easter21User> getCache() {
		return cache;
	}

}
