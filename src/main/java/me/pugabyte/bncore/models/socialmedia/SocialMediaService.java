package me.pugabyte.bncore.models.socialmedia;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SocialMediaUser.class)
public class SocialMediaService extends MongoService {
	private final static Map<UUID, SocialMediaUser> cache = new HashMap<>();

	public Map<UUID, SocialMediaUser> getCache() {
		return cache;
	}

}
