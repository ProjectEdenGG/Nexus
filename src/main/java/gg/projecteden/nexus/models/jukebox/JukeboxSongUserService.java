package gg.projecteden.nexus.models.jukebox;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(JukeboxSongUser.class)
public class JukeboxSongUserService extends MongoService<JukeboxSongUser> {
	private final static Map<UUID, JukeboxSongUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, JukeboxSongUser> getCache() {
		return cache;
	}

}
