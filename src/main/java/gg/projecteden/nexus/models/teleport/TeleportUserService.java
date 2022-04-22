package gg.projecteden.nexus.models.teleport;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(TeleportUser.class)
public class TeleportUserService extends MongoPlayerService<TeleportUser> {
	private final static Map<UUID, TeleportUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, TeleportUser> getCache() {
		return cache;
	}

}
