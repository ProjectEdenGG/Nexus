package gg.projecteden.nexus.models.legacy.itemtransfer;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(LegacyItemTransferUser.class)
public class LegacyItemTransferUserService extends MongoPlayerService<LegacyItemTransferUser> {
	private final static Map<UUID, LegacyItemTransferUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, LegacyItemTransferUser> getCache() {
		return cache;
	}

}
