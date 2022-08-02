package gg.projecteden.nexus.models.hub;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(HubParkourUser.class)
public class HubParkourUserService extends MongoPlayerService<HubParkourUser> {
	private final static Map<UUID, HubParkourUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, HubParkourUser> getCache() {
		return cache;
	}

}
