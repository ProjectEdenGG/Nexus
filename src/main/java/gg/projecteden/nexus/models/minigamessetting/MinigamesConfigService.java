package gg.projecteden.nexus.models.minigamessetting;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(MinigamesConfig.class)
public class MinigamesConfigService extends MongoPlayerService<MinigamesConfig> {
	private final static Map<UUID, MinigamesConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, MinigamesConfig> getCache() {
		return cache;
	}

}
