package me.pugabyte.bncore.models.newrankcolors;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(NewRankColors.class)
public class NewRankColorsService extends MongoService {
	private final static Map<UUID, NewRankColors> cache = new HashMap<>();

	public Map<UUID, NewRankColors> getCache() {
		return cache;
	}

}
