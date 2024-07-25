package gg.projecteden.nexus.models.vulan24;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(VulanLanternUser.class)
public class VuLan24LanternService extends MongoPlayerService<VulanLanternUser> {
	private final static Map<UUID, VulanLanternUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, VulanLanternUser> getCache() {
		return cache;
	}

}
