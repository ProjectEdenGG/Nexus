package gg.projecteden.nexus.models.vaults;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(VaultUser.class)
public class VaultUserService extends MongoPlayerService<VaultUser> {
	private final static Map<UUID, VaultUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, VaultUser> getCache() {
		return cache;
	}

}
