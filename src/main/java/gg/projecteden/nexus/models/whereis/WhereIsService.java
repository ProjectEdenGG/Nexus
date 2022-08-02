package gg.projecteden.nexus.models.whereis;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(WhereIs.class)
public class WhereIsService extends MongoPlayerService<WhereIs> {
	private final static Map<UUID, WhereIs> cache = new ConcurrentHashMap<>();

	public Map<UUID, WhereIs> getCache() {
		return cache;
	}

}
