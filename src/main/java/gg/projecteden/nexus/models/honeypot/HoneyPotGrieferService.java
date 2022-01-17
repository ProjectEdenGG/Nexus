package gg.projecteden.nexus.models.honeypot;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(HoneyPotGriefer.class)
public class HoneyPotGrieferService extends MongoPlayerService<HoneyPotGriefer> {
	private final static Map<UUID, HoneyPotGriefer> cache = new ConcurrentHashMap<>();

	public Map<UUID, HoneyPotGriefer> getCache() {
		return cache;
	}

}
