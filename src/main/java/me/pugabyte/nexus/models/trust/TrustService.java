package me.pugabyte.nexus.models.trust;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Trust.class)
public class TrustService extends MongoService<Trust> {
	private final static Map<UUID, Trust> cache = new HashMap<>();

	public Map<UUID, Trust> getCache() {
		return cache;
	}

}
