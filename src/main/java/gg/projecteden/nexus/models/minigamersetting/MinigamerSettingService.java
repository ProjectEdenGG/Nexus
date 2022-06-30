package gg.projecteden.nexus.models.minigamersetting;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(MinigamerSetting.class)
public class MinigamerSettingService extends MongoPlayerService<MinigamerSetting> {
	private final static Map<UUID, MinigamerSetting> cache = new ConcurrentHashMap<>();

	public Map<UUID, MinigamerSetting> getCache() {
		return cache;
	}

}
