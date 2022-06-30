package gg.projecteden.nexus.models.dnd;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DNDUser.class)
public class DNDUserService extends MongoPlayerService<DNDUser> {
	private final static Map<UUID, DNDUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, DNDUser> getCache() {
		return cache;
	}

}
