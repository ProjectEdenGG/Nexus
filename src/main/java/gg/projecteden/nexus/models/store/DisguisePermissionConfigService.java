package gg.projecteden.nexus.models.store;

import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.api.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DisguisePermissionConfig.class)
public class DisguisePermissionConfigService extends MongoService<DisguisePermissionConfig> {
	private final static Map<UUID, DisguisePermissionConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, DisguisePermissionConfig> getCache() {
		return cache;
	}

}
