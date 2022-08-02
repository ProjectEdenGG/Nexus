package gg.projecteden.nexus.models.playerplushie;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(PlayerPlushieUser.class)
public class PlayerPlushieUserService extends MongoPlayerService<PlayerPlushieUser> {
	private final static Map<UUID, PlayerPlushieUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, PlayerPlushieUser> getCache() {
		return cache;
	}

}
