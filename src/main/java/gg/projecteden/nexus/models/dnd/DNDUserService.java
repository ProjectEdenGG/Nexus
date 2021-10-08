package gg.projecteden.nexus.models.dnd;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(DNDUser.class)
public class DNDUserService extends MongoService<DNDUser> {
	private final static Map<UUID, DNDUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, DNDUser> getCache() {
		return cache;
	}

}
