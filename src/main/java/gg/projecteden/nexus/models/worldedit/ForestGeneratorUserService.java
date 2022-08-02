package gg.projecteden.nexus.models.worldedit;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ForestGeneratorUser.class)
public class ForestGeneratorUserService extends MongoPlayerService<ForestGeneratorUser> {
	private final static Map<UUID, ForestGeneratorUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, ForestGeneratorUser> getCache() {
		return cache;
	}

}
