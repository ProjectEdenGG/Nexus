package gg.projecteden.nexus.models.bossfight;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(BossFightUser.class)
public class BossFightUserService extends MongoPlayerService<BossFightUser> {
	private final static Map<UUID, BossFightUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, BossFightUser> getCache() {
		return cache;
	}

}
