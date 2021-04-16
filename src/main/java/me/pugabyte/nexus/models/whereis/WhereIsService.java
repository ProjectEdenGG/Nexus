package me.pugabyte.nexus.models.whereis;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(WhereIs.class)
public class WhereIsService extends MongoService<WhereIs> {
	private final static Map<UUID, WhereIs> cache = new HashMap<>();

	public Map<UUID, WhereIs> getCache() {
		return cache;
	}

}
