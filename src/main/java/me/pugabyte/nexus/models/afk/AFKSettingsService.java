package me.pugabyte.nexus.models.afk;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AFKSettings.class)
public class AFKSettingsService extends MongoService<AFKSettings> {
	private final static Map<UUID, AFKSettings> cache = new HashMap<>();

	public Map<UUID, AFKSettings> getCache() {
		return cache;
	}

}
