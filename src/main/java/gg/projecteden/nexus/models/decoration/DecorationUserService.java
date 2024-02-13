package gg.projecteden.nexus.models.decoration;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DecorationUser.class)
public class DecorationUserService extends MongoPlayerService<DecorationUser> {
	private final static Map<UUID, DecorationUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, DecorationUser> getCache() {
		return cache;
	}
}
