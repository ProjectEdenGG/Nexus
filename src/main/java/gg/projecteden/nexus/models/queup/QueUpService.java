package gg.projecteden.nexus.models.queup;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(QueUp.class)
public class QueUpService extends MongoPlayerService<QueUp> {
	private final static Map<UUID, QueUp> cache = new ConcurrentHashMap<>();

	public Map<UUID, QueUp> getCache() {
		return cache;
	}

}
