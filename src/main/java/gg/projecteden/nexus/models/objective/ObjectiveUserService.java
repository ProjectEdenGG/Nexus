package gg.projecteden.nexus.models.objective;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ObjectiveUser.class)
public class ObjectiveUserService extends MongoPlayerService<ObjectiveUser> {
	private final static Map<UUID, ObjectiveUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, ObjectiveUser> getCache() {
		return cache;
	}

}
