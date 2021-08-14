package gg.projecteden.nexus.models.afk;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(AFKUser.class)
public class AFKUserService extends MongoService<AFKUser> {
	private final static Map<UUID, AFKUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, AFKUser> getCache() {
		return cache;
	}

}
