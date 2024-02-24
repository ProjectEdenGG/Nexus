package gg.projecteden.nexus.models.offline;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(OfflineMessageUser.class)
public class OfflineMessageService extends MongoPlayerService<OfflineMessageUser> {
	private final static Map<UUID, OfflineMessageUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, OfflineMessageUser> getCache() {
		return cache;
	}
}
