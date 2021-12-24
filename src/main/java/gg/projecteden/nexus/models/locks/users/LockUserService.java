package gg.projecteden.nexus.models.locks.users;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(LockUser.class)
public class LockUserService extends MongoPlayerService<LockUser> {
	private final static Map<UUID, LockUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, LockUser> getCache() {
		return cache;
	}

}
