package me.pugabyte.nexus.models.socialmedia;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(TwitterData.class)
public class TwitterService extends MongoService {
	private final static Map<UUID, TwitterData> cache = new HashMap<>();

	public Map<UUID, TwitterData> getCache() {
		return cache;
	}

	public TwitterData get() {
		return get(Nexus.getUUID0());
	}

}
