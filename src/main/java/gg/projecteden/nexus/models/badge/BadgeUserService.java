package gg.projecteden.nexus.models.badge;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(BadgeUser.class)
public class BadgeUserService extends MongoService<BadgeUser> {
	private final static Map<UUID, BadgeUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, BadgeUser> getCache() {
		return cache;
	}

}
