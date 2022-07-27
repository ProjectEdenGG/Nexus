package gg.projecteden.nexus.models.honeypot;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(HoneyPotBans.class)
public class HoneyPotBansService extends MongoPlayerService<HoneyPotBans> {
	private final static Map<UUID, HoneyPotBans> cache = new ConcurrentHashMap<>();

	public Map<UUID, HoneyPotBans> getCache() {
		return cache;
	}

}
