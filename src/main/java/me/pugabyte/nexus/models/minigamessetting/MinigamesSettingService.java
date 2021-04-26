package me.pugabyte.nexus.models.minigamessetting;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(MinigamesSetting.class)
public class MinigamesSettingService extends MongoService<MinigamesSetting> {
	private final static Map<UUID, MinigamesSetting> cache = new HashMap<>();

	public Map<UUID, MinigamesSetting> getCache() {
		return cache;
	}

	public MinigamesSetting get() {
		return get(Nexus.getUUID0());
	}
}
