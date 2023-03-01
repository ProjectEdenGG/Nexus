package gg.projecteden.nexus.models.parkour;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(LobbyParkourUser.class)
public class LobbyParkourUserService extends MongoPlayerService<LobbyParkourUser> {
	private final static Map<UUID, LobbyParkourUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, LobbyParkourUser> getCache() {
		return cache;
	}

}
