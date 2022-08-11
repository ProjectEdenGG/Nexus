package gg.projecteden.nexus.models.friends;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(FriendsUser.class)
public class FriendsUserService extends MongoPlayerService<FriendsUser> {
	private final static Map<UUID, FriendsUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, FriendsUser> getCache() {
		return cache;
	}
}
