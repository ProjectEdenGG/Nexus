package gg.projecteden.nexus.models.fakenpcs.users;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(FakeNPCUser.class)
public class FakeNPCUserService extends MongoPlayerService<FakeNPCUser> {
	private final static Map<UUID, FakeNPCUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, FakeNPCUser> getCache() {
		return cache;
	}

}
