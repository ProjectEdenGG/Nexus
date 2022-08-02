package gg.projecteden.nexus.models.freeze;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Freeze.class)
public class FreezeService extends MongoPlayerService<Freeze> {
	private final static Map<UUID, Freeze> cache = new ConcurrentHashMap<>();

	public Map<UUID, Freeze> getCache() {
		return cache;
	}

}
