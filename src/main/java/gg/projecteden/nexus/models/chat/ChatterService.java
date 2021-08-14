package gg.projecteden.nexus.models.chat;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Chatter.class)
public class ChatterService extends MongoService<Chatter> {
	private final static Map<UUID, Chatter> cache = new ConcurrentHashMap<>();

	public Map<UUID, Chatter> getCache() {
		return cache;
	}

}
