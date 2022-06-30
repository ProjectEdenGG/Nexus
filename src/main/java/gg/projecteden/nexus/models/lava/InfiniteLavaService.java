package gg.projecteden.nexus.models.lava;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(InfiniteLava.class)
public class InfiniteLavaService extends MongoPlayerService<InfiniteLava> {
	private final static Map<UUID, InfiniteLava> cache = new ConcurrentHashMap<>();

	public Map<UUID, InfiniteLava> getCache() {
		return cache;
	}

}
