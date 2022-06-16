package gg.projecteden.nexus.models.jukebox;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(JukeboxUser.class)
public class JukeboxUserService extends MongoPlayerService<JukeboxUser> {
	private final static Map<UUID, JukeboxUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, JukeboxUser> getCache() {
		return cache;
	}

	@Override
	public void clearCache() {
		for (JukeboxUser user : getCache().values())
			if (user.getSongPlayer() != null) {
				user.getSongPlayer().destroy();
				user.setSongPlayer(null);
			}

		super.clearCache();
	}

}
