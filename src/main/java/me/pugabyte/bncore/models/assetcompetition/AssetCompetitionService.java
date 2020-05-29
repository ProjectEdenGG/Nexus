package me.pugabyte.bncore.models.assetcompetition;

import lombok.Getter;
import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AssetCompetition.class)
public class AssetCompetitionService extends MongoService {
	@Getter
	private final static Map<UUID, AssetCompetition> cache = new HashMap<>();

	public Map<UUID, AssetCompetition> getCache() {
		return cache;
	}

}
