package gg.projecteden.nexus.models.assetcompetition;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(AssetCompetition.class)
public class AssetCompetitionService extends MongoService<AssetCompetition> {
	private final static Map<UUID, AssetCompetition> cache = new ConcurrentHashMap<>();

	public Map<UUID, AssetCompetition> getCache() {
		return cache;
	}

}
