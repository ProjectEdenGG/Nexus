package gg.projecteden.nexus.models.teleport;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(TeleportRequests.class)
public class TeleportRequestsService extends MongoBukkitService<TeleportRequests> {
	private final static Map<UUID, TeleportRequests> cache = new ConcurrentHashMap<>();

	public Map<UUID, TeleportRequests> getCache() {
		return cache;
	}

}
