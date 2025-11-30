package gg.projecteden.nexus.models.minigolf;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(MiniGolfUser.class)
public class MiniGolfUserService extends MongoPlayerService<MiniGolfUser> {
	private final static Map<UUID, MiniGolfUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, MiniGolfUser> getCache() {
		return cache;
	}

}
