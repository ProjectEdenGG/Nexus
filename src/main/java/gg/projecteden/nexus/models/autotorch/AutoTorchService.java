package gg.projecteden.nexus.models.autotorch;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(AutoTorchUser.class)
public class AutoTorchService extends MongoService<AutoTorchUser> {
	private static final Map<UUID, AutoTorchUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, AutoTorchUser> getCache() {
		return cache;
	}

}
