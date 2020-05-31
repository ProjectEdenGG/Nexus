package me.pugabyte.bncore.models.trust;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Trust.class)
public class TrustService extends MongoService {
	private final static Map<UUID, Trust> cache = new HashMap<>();

	public Map<UUID, Trust> getCache() {
		return cache;
	}

}
