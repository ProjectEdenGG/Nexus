package gg.projecteden.nexus.models.trust;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(TrustsUser.class)
public class TrustsUserService extends MongoPlayerService<TrustsUser> {
	private final static Map<UUID, TrustsUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, TrustsUser> getCache() {
		return cache;
	}

}
