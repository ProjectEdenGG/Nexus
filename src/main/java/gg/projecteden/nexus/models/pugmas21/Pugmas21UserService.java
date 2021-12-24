package gg.projecteden.nexus.models.pugmas21;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Pugmas21User.class)
public class Pugmas21UserService extends MongoPlayerService<Pugmas21User> {
	private final static Map<UUID, Pugmas21User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Pugmas21User> getCache() {
		return cache;
	}

}
