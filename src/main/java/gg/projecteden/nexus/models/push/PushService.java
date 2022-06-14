package gg.projecteden.nexus.models.push;

import gg.projecteden.mongodb.MongoService;
import gg.projecteden.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(PushUser.class)
public class PushService extends MongoService<PushUser> {
	private final static Map<UUID, PushUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, PushUser> getCache() {
		return cache;
	}

}
