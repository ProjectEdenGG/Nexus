package me.pugabyte.nexus.models.queup;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Queup.class)
public class QueupService extends MongoService {
	private final static Map<UUID, Queup> cache = new HashMap<>();

	public Map<UUID, Queup> getCache() {
		return cache;
	}

	public Queup get() {
		return get(Nexus.getUUID0());
	}

}
