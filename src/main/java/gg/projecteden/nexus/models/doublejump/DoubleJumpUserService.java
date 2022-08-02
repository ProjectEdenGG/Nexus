package gg.projecteden.nexus.models.doublejump;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DoubleJumpUser.class)
public class DoubleJumpUserService extends MongoPlayerService<DoubleJumpUser> {
	private final static Map<UUID, DoubleJumpUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, DoubleJumpUser> getCache() {
		return cache;
	}

}
