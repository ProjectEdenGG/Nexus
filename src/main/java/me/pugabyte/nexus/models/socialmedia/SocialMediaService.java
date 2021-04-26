package me.pugabyte.nexus.models.socialmedia;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SocialMediaUser.class)
public class SocialMediaService extends MongoService<SocialMediaUser> {
	private final static Map<UUID, SocialMediaUser> cache = new HashMap<>();

	public Map<UUID, SocialMediaUser> getCache() {
		return cache;
	}

}
