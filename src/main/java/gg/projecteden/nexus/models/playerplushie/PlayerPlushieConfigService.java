package gg.projecteden.nexus.models.playerplushie;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(PlayerPlushieConfig.class)
public class PlayerPlushieConfigService extends MongoPlayerService<PlayerPlushieConfig> {
	private final static Map<UUID, PlayerPlushieConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, PlayerPlushieConfig> getCache() {
		return cache;
	}

}
