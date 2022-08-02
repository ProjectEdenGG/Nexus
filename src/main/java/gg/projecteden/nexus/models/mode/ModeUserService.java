package gg.projecteden.nexus.models.mode;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ModeUser.class)
public class ModeUserService extends MongoPlayerService<ModeUser> {
	private final static Map<UUID, ModeUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, ModeUser> getCache() {
		return cache;
	}
}
