package gg.projecteden.nexus.models.virtualinventories;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(VirtualInventoriesConfig.class)
public class VirtualInventoriesConfigService extends MongoBukkitService<VirtualInventoriesConfig> {
	private final static Map<UUID, VirtualInventoriesConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, VirtualInventoriesConfig> getCache() {
		return cache;
	}

}
