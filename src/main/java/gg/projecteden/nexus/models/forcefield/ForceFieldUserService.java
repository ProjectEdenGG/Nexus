package gg.projecteden.nexus.models.forcefield;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(ForceFieldUser.class)
public class ForceFieldUserService extends MongoService<ForceFieldUser> {
	private final static Map<UUID, ForceFieldUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, ForceFieldUser> getCache() {
		return cache;
	}
}
