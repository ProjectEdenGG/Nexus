package me.pugabyte.nexus.models.invisiblearmour;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(InvisibleArmor.class)
public class InvisibleArmorService extends MongoService<InvisibleArmor> {
	private final static Map<UUID, InvisibleArmor> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, InvisibleArmor> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
