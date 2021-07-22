package gg.projecteden.nexus.models.ambience;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(AmbienceUser.class)
public class AmbienceUserService extends MongoService<AmbienceUser> {
	private final static Map<UUID, AmbienceUser> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	@Override
	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	public Map<UUID, AmbienceUser> getCache() {
		return cache;
	}
}
