package me.pugabyte.bncore.models.socialmedia;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SocialMedia.class)
public class SocialMediaService extends MongoService {
	private final static Map<UUID, SocialMedia> cache = new HashMap<>();

	public Map<UUID, SocialMedia> getCache() {
		return cache;
	}

}
