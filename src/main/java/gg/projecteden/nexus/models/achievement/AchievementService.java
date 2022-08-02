package gg.projecteden.nexus.models.achievement;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(AchievementPlayer.class)
public class AchievementService extends MongoPlayerService<AchievementPlayer> {
	private final static Map<UUID, AchievementPlayer> cache = new ConcurrentHashMap<>();

	public Map<UUID, AchievementPlayer> getCache() {
		return cache;
	}

}
