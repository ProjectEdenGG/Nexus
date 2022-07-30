package gg.projecteden.nexus.models.monthlypodium;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(MonthlyPodiumUser.class)
public class MonthlyPodiumUserService extends MongoPlayerService<MonthlyPodiumUser> {
	private final static Map<UUID, MonthlyPodiumUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, MonthlyPodiumUser> getCache() {
		return cache;
	}

}
