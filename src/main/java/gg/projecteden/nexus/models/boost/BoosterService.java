package gg.projecteden.nexus.models.boost;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Booster.class)
public class BoosterService extends MongoPlayerService<Booster> {
	private final static Map<UUID, Booster> cache = new ConcurrentHashMap<>();

	public Map<UUID, Booster> getCache() {
		return cache;
	}

}
