package gg.projecteden.nexus.models.halloween25;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Halloween25User.class)
public class Halloween25UserService extends MongoPlayerService<Halloween25User> {
	private final static Map<UUID, Halloween25User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Halloween25User> getCache() {
		return cache;
	}

}
