package gg.projecteden.nexus.models.badge;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(BadgeUser.class)
public class BadgeUserService extends MongoPlayerService<BadgeUser> {
	private final static Map<UUID, BadgeUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, BadgeUser> getCache() {
		return cache;
	}

}
