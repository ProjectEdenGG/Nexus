package gg.projecteden.nexus.models.jukebox;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(JukeboxUser.class)
public class JukeboxUserService extends MongoService<JukeboxUser> {
	private final static Map<UUID, JukeboxUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, JukeboxUser> getCache() {
		return cache;
	}

}
