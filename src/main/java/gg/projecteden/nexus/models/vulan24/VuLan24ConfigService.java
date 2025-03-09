package gg.projecteden.nexus.models.vulan24;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(VuLan24Config.class)
public class VuLan24ConfigService extends MongoBukkitService<VuLan24Config> {
	private final static Map<UUID, VuLan24Config> cache = new ConcurrentHashMap<>();

	public Map<UUID, VuLan24Config> getCache() {
		return cache;
	}

}
