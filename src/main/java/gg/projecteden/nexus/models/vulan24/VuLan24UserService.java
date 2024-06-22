package gg.projecteden.nexus.models.vulan24;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(VuLan24User.class)
public class VuLan24UserService extends MongoPlayerService<VuLan24User> {
	private final static Map<UUID, VuLan24User> cache = new ConcurrentHashMap<>();

	public Map<UUID, VuLan24User> getCache() {
		return cache;
	}

}
