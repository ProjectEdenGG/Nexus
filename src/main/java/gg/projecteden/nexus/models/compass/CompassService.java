package gg.projecteden.nexus.models.compass;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Compass.class)
public class CompassService extends MongoPlayerService<Compass> {
	private final static Map<UUID, Compass> cache = new ConcurrentHashMap<>();

	public Map<UUID, Compass> getCache() {
		return cache;
	}

}
