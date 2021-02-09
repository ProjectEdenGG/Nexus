package me.pugabyte.nexus.models.nameban;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AssetCompetition.class)
public class NameBanConfigService extends MongoService {
	private final static Map<UUID, AssetCompetition> cache = new HashMap<>();

	public Map<UUID, AssetCompetition> getCache() {
		return cache;
	}

}
