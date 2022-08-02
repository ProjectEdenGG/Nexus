package gg.projecteden.nexus.models.halloween20;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Halloween20User.class)
public class Halloween20Service extends MongoPlayerService<Halloween20User> {
	private final static Map<UUID, Halloween20User> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, Halloween20User> getCache() {
		return cache;
	}
}
