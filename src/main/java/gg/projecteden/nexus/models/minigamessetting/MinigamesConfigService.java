package gg.projecteden.nexus.models.minigamessetting;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(MinigamesConfig.class)
public class MinigamesConfigService extends MongoService<MinigamesConfig> {
	private final static Map<UUID, MinigamesConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, MinigamesConfig> getCache() {
		return cache;
	}

}
