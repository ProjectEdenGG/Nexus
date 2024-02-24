package gg.projecteden.nexus.models.voter;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(VotePartyData.class)
public class VotePartyService extends MongoPlayerService<VotePartyData> {
	private final static Map<UUID, VotePartyData> cache = new ConcurrentHashMap<>();

	public Map<UUID, VotePartyData> getCache() {
		return cache;
	}

}
