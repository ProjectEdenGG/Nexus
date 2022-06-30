package gg.projecteden.nexus.models.afk;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(AFKUser.class)
public class AFKUserService extends MongoPlayerService<AFKUser> {
	private final static Map<UUID, AFKUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, AFKUser> getCache() {
		return cache;
	}

}
