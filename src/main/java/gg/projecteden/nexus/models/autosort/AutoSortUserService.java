package gg.projecteden.nexus.models.autosort;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(AutoSortUser.class)
public class AutoSortUserService extends MongoPlayerService<AutoSortUser> {
	private final static Map<UUID, AutoSortUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, AutoSortUser> getCache() {
		return cache;
	}

}
