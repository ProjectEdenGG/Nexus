package gg.projecteden.nexus.models.mode;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(ModeUser.class)
public class ModeUserService extends MongoService<ModeUser> {
	private final static Map<UUID, ModeUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, ModeUser> getCache() {
		return cache;
	}
}
