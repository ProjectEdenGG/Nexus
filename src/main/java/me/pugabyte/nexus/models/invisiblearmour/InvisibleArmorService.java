package me.pugabyte.nexus.models.invisiblearmour;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(InvisibleArmor.class)
public class InvisibleArmorService extends MongoService<InvisibleArmor> {
	private final static Map<UUID, InvisibleArmor> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, InvisibleArmor> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
