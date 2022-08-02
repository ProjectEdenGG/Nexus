package gg.projecteden.nexus.models.mcmmo;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(McMMOPrestigeUser.class)
public class McMMOPrestigeUserService extends MongoPlayerService<McMMOPrestigeUser> {
	private final static Map<UUID, McMMOPrestigeUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, McMMOPrestigeUser> getCache() {
		return cache;
	}

}
