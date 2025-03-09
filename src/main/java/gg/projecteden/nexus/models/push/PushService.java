package gg.projecteden.nexus.models.push;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(PushUser.class)
public class PushService extends MongoBukkitService<PushUser> {
	private final static Map<UUID, PushUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, PushUser> getCache() {
		return cache;
	}

}
