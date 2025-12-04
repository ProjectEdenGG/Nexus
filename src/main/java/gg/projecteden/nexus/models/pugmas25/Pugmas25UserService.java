package gg.projecteden.nexus.models.pugmas25;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Pugmas25User.class)
public class Pugmas25UserService extends MongoPlayerService<Pugmas25User> {
	private final static Map<UUID, Pugmas25User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Pugmas25User> getCache() {
		return cache;
	}

	public void resetAllAnglerQuests() {
		for (var user : cacheAll())
			user.resetAnglerQuest();
		saveCache();
	}

}
