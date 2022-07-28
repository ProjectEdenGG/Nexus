package gg.projecteden.nexus.models.socialmedia;

import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.api.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(InstagramData.class)
public class InstagramService extends MongoService<InstagramData> {
	private final static Map<UUID, InstagramData> cache = new ConcurrentHashMap<>();

	public Map<UUID, InstagramData> getCache() {
		return cache;
	}

}
