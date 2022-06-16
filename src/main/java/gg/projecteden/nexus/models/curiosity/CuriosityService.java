package gg.projecteden.nexus.models.curiosity;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Curiosity.class)
public class CuriosityService extends MongoPlayerService<Curiosity> {
	private final static Map<UUID, Curiosity> cache = new ConcurrentHashMap<>();

	public Map<UUID, Curiosity> getCache() {
		return cache;
	}

}
