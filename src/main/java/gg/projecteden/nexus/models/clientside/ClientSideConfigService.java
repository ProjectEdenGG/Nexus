package gg.projecteden.nexus.models.clientside;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ClientSideConfig.class)
public class ClientSideConfigService extends MongoPlayerService<ClientSideConfig> {
	private final static Map<UUID, ClientSideConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, ClientSideConfig> getCache() {
		return cache;
	}

}
