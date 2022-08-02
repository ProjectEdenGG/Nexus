package gg.projecteden.nexus.models.autoinventory;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(AutoInventoryUser.class)
public class AutoInventoryUserService extends MongoPlayerService<AutoInventoryUser> {
	private final static Map<UUID, AutoInventoryUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, AutoInventoryUser> getCache() {
		return cache;
	}

}
