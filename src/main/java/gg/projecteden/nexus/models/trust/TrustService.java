package gg.projecteden.nexus.models.trust;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Trust.class)
public class TrustService extends MongoService<Trust> {
	private final static Map<UUID, Trust> cache = new ConcurrentHashMap<>();

	public Map<UUID, Trust> getCache() {
		return cache;
	}

}
