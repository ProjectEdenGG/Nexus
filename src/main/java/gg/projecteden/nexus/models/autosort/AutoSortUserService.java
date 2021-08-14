package gg.projecteden.nexus.models.autosort;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(AutoSortUser.class)
public class AutoSortUserService extends MongoService<AutoSortUser> {
	private final static Map<UUID, AutoSortUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, AutoSortUser> getCache() {
		return cache;
	}

}
