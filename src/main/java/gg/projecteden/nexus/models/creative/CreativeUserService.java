package gg.projecteden.nexus.models.creative;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(CreativeUser.class)
public class CreativeUserService extends MongoPlayerService<CreativeUser> {
	private final static Map<UUID, CreativeUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, CreativeUser> getCache() {
		return cache;
	}

}
