package gg.projecteden.nexus.models.party;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(PartyUser.class)
public class PartyUserService extends MongoPlayerService<PartyUser> {

	private final static Map<UUID, PartyUser> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, PartyUser> getCache() {
		return cache;
	}
}
