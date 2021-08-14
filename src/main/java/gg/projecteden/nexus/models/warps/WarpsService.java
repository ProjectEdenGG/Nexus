package gg.projecteden.nexus.models.warps;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Warps.class)
public class WarpsService extends MongoService<Warps> {
	private final static Map<UUID, Warps> cache = new ConcurrentHashMap<>();

	public Map<UUID, Warps> getCache() {
		return cache;
	}

}
