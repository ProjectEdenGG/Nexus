package gg.projecteden.nexus.models.nameplates;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(NameplateUser.class)
public class NameplateUserService extends MongoPlayerService<NameplateUser> {
	private final static Map<UUID, NameplateUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, NameplateUser> getCache() {
		return cache;
	}

}
