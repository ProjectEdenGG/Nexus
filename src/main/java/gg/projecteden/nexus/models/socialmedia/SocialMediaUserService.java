package gg.projecteden.nexus.models.socialmedia;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(SocialMediaUser.class)
public class SocialMediaUserService extends MongoService<SocialMediaUser> {
	private final static Map<UUID, SocialMediaUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, SocialMediaUser> getCache() {
		return cache;
	}

}
