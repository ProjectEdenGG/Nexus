package gg.projecteden.nexus.models.pride21;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Pride21User.class)
public class Pride21UserService extends MongoPlayerService<Pride21User> {
	private static final Map<UUID, Pride21User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Pride21User> getCache() {
		return cache;
	}

}
