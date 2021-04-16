package me.pugabyte.nexus.models.honeypot;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(HoneyPotBans.class)
public class HoneyPotBansService extends MongoService<HoneyPotBans> {
	private final static Map<UUID, HoneyPotBans> cache = new HashMap<>();

	public Map<UUID, HoneyPotBans> getCache() {
		return cache;
	}

}
