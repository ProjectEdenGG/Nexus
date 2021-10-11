package gg.projecteden.nexus.models.playerplushie;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(PlayerPlushieConfig.class)
public class PlayerPlushieConfigService extends MongoService<PlayerPlushieConfig> {
	private final static Map<UUID, PlayerPlushieConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, PlayerPlushieConfig> getCache() {
		return cache;
	}

}
