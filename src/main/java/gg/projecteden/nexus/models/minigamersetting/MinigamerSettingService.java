package gg.projecteden.nexus.models.minigamersetting;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(MinigamerSetting.class)
public class MinigamerSettingService extends MongoService<MinigamerSetting> {
	private final static Map<UUID, MinigamerSetting> cache = new ConcurrentHashMap<>();

	public Map<UUID, MinigamerSetting> getCache() {
		return cache;
	}

}
