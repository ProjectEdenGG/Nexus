package gg.projecteden.nexus.models.legacy;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(LegacyUser.class)
public class LegacyUserService extends MongoPlayerService<LegacyUser> {
	private final static Map<UUID, LegacyUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, LegacyUser> getCache() {
		return cache;
	}

}
