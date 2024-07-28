package gg.projecteden.nexus.models.virtualinventories;

import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.api.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(VirtualInventoriesConfig.class)
public class VirtualInventoriesConfigService extends MongoService<VirtualInventoriesConfig> {
	private final static Map<UUID, VirtualInventoriesConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, VirtualInventoriesConfig> getCache() {
		return cache;
	}

}
