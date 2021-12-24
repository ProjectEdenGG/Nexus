package gg.projecteden.nexus.models.bearfair21;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(BearFair21User.class)
public class BearFair21UserService extends MongoPlayerService<BearFair21User> {
	private final static Map<UUID, BearFair21User> cache = new ConcurrentHashMap<>();

	public Map<UUID, BearFair21User> getCache() {
		return cache;
	}

	@Override
	protected void beforeDelete(BearFair21User user) {
		user.cancelActiveTask();
	}

}
