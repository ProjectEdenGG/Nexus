package gg.projecteden.nexus.models.assetcompetition;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(AssetCompetition.class)
public class AssetCompetitionService extends MongoPlayerService<AssetCompetition> {
	private final static Map<UUID, AssetCompetition> cache = new ConcurrentHashMap<>();

	public Map<UUID, AssetCompetition> getCache() {
		return cache;
	}

}
