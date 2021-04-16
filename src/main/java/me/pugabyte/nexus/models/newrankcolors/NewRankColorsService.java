package me.pugabyte.nexus.models.newrankcolors;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(NewRankColors.class)
public class NewRankColorsService extends MongoService<NewRankColors> {
	private final static Map<UUID, NewRankColors> cache = new HashMap<>();

	public Map<UUID, NewRankColors> getCache() {
		return cache;
	}

}
