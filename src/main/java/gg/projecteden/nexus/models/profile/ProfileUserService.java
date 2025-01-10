package gg.projecteden.nexus.models.profile;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ProfileUser.class)
public class ProfileUserService extends MongoPlayerService<ProfileUser> {
	private static final Map<UUID, ProfileUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, ProfileUser> getCache() {
		return cache;
	}
}
