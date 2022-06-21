package gg.projecteden.nexus.models.resourcepack;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(LocalResourcePackUser.class)
public class LocalResourcePackUserService extends MongoPlayerService<LocalResourcePackUser> {
	private final static Map<UUID, LocalResourcePackUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, LocalResourcePackUser> getCache() {
		return cache;
	}

}
