package me.pugabyte.nexus.models.minigamersetting;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(MinigamerSetting.class)
public class MinigamerSettingService extends MongoService<MinigamerSetting> {
	private final static Map<UUID, MinigamerSetting> cache = new HashMap<>();

	public Map<UUID, MinigamerSetting> getCache() {
		return cache;
	}

}
