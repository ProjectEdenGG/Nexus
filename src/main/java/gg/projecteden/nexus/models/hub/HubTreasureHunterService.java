package gg.projecteden.nexus.models.hub;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(HubTreasureHunter.class)
public class HubTreasureHunterService extends MongoPlayerService<HubTreasureHunter> {
	private final static Map<UUID, HubTreasureHunter> cache = new ConcurrentHashMap<>();

	public Map<UUID, HubTreasureHunter> getCache() {
		return cache;
	}

}
