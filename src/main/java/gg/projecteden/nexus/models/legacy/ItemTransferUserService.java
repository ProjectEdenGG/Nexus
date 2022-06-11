package gg.projecteden.nexus.models.legacy;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ItemTransferUser.class)
public class ItemTransferUserService extends MongoPlayerService<ItemTransferUser> {
	private final static Map<UUID, ItemTransferUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, ItemTransferUser> getCache() {
		return cache;
	}

}
