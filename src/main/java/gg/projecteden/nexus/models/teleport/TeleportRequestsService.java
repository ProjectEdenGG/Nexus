package gg.projecteden.nexus.models.teleport;

import gg.projecteden.mongodb.MongoService;
import gg.projecteden.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(TeleportRequests.class)
public class TeleportRequestsService extends MongoService<TeleportRequests> {
	private final static Map<UUID, TeleportRequests> cache = new ConcurrentHashMap<>();

	public Map<UUID, TeleportRequests> getCache() {
		return cache;
	}

}
