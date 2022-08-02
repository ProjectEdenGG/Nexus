package gg.projecteden.nexus.models.halloween21;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Halloween21User.class)
public class Halloween21UserService extends MongoPlayerService<Halloween21User> {
	private final static Map<UUID, Halloween21User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Halloween21User> getCache() {
		return cache;
	}

}
