package gg.projecteden.nexus.models.socialmedia;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(SocialMediaUser.class)
public class SocialMediaUserService extends MongoPlayerService<SocialMediaUser> {
	private final static Map<UUID, SocialMediaUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, SocialMediaUser> getCache() {
		return cache;
	}

}
