package gg.projecteden.nexus.models.clientsideentities;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ClientSideEntitiesConfig.class)
public class ClientSideEntitiesConfigService extends MongoPlayerService<ClientSideEntitiesConfig> {
	private final static Map<UUID, ClientSideEntitiesConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, ClientSideEntitiesConfig> getCache() {
		return cache;
	}

}
