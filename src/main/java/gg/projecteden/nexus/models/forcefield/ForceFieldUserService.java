package gg.projecteden.nexus.models.forcefield;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ForceFieldUser.class)
public class ForceFieldUserService extends MongoPlayerService<ForceFieldUser> {
	private final static Map<UUID, ForceFieldUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, ForceFieldUser> getCache() {
		return cache;
	}
}
