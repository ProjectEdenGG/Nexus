package gg.projecteden.nexus.models.clientsideentities;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(ClientSideEntitiesConfig.class)
public class ClientSideEntitiesConfigService extends MongoService<ClientSideEntitiesConfig> {
	private final static Map<UUID, ClientSideEntitiesConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, ClientSideEntitiesConfig> getCache() {
		return cache;
	}

}
