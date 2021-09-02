package gg.projecteden.nexus.models.nameplates;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(NameplateUser.class)
public class NameplateUserService extends MongoService<NameplateUser> {
	private final static Map<UUID, NameplateUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, NameplateUser> getCache() {
		return cache;
	}

}
