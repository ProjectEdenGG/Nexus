package gg.projecteden.nexus.models.ambience;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(AmbienceUser.class)
public class AmbienceUserService extends MongoService<AmbienceUser> {
	private final static Map<UUID, AmbienceUser> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, AmbienceUser> getCache() {
		return cache;
	}
}
