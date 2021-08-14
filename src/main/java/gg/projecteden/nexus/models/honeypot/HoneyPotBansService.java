package gg.projecteden.nexus.models.honeypot;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(HoneyPotBans.class)
public class HoneyPotBansService extends MongoService<HoneyPotBans> {
	private final static Map<UUID, HoneyPotBans> cache = new ConcurrentHashMap<>();

	public Map<UUID, HoneyPotBans> getCache() {
		return cache;
	}

}
