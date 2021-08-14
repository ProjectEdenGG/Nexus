package gg.projecteden.nexus.models.freeze;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Freeze.class)
public class FreezeService extends MongoService<Freeze> {
	private final static Map<UUID, Freeze> cache = new ConcurrentHashMap<>();

	public Map<UUID, Freeze> getCache() {
		return cache;
	}

}
