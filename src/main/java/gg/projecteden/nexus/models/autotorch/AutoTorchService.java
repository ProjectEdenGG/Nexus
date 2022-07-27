package gg.projecteden.nexus.models.autotorch;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(AutoTorchUser.class)
public class AutoTorchService extends MongoPlayerService<AutoTorchUser> {
	private static final Map<UUID, AutoTorchUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, AutoTorchUser> getCache() {
		return cache;
	}

}
