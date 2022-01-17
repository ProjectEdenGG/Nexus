package gg.projecteden.nexus.models.socialmedia;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(TwitterData.class)
public class TwitterService extends MongoPlayerService<TwitterData> {
	private final static Map<UUID, TwitterData> cache = new ConcurrentHashMap<>();

	public Map<UUID, TwitterData> getCache() {
		return cache;
	}

}
