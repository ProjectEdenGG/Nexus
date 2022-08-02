package gg.projecteden.nexus.models.clientside;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ClientSideUser.class)
public class ClientSideUserService extends MongoPlayerService<ClientSideUser> {
	private final static Map<UUID, ClientSideUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, ClientSideUser> getCache() {
		return cache;
	}

}
