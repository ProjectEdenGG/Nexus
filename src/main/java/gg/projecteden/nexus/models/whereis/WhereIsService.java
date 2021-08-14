package gg.projecteden.nexus.models.whereis;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(WhereIs.class)
public class WhereIsService extends MongoService<WhereIs> {
	private final static Map<UUID, WhereIs> cache = new ConcurrentHashMap<>();

	public Map<UUID, WhereIs> getCache() {
		return cache;
	}

}
