package gg.projecteden.nexus.models.warps;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Warps.class)
public class WarpsService extends MongoPlayerService<Warps> {
	private final static Map<UUID, Warps> cache = new ConcurrentHashMap<>();

	public Map<UUID, Warps> getCache() {
		return cache;
	}

}
