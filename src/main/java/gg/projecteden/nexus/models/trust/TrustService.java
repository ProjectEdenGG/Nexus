package gg.projecteden.nexus.models.trust;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Trust.class)
public class TrustService extends MongoPlayerService<Trust> {
	private final static Map<UUID, Trust> cache = new ConcurrentHashMap<>();

	public Map<UUID, Trust> getCache() {
		return cache;
	}

}
