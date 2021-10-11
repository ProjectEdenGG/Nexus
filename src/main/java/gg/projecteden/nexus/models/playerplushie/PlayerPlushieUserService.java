package gg.projecteden.nexus.models.playerplushie;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(PlayerPlushieUser.class)
public class PlayerPlushieUserService extends MongoService<PlayerPlushieUser> {
	private final static Map<UUID, PlayerPlushieUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, PlayerPlushieUser> getCache() {
		return cache;
	}

}
