package me.pugabyte.nexus.models.banker;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Banker.class)
public class BankerService extends MongoService {
	private final static Map<UUID, Banker> cache = new HashMap<>();

	public Map<UUID, Banker> getCache() {
		return cache;
	}

}
