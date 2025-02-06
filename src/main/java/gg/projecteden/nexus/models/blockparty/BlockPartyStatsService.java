package gg.projecteden.nexus.models.blockparty;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(BlockPartyStatsUser.class)
public class BlockPartyStatsService extends MongoPlayerService<BlockPartyStatsUser> {
	private final static Map<UUID, BlockPartyStatsUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, BlockPartyStatsUser> getCache() {
		return cache;
	}

}
