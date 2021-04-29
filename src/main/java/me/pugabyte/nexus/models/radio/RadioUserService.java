package me.pugabyte.nexus.models.radio;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(RadioUser.class)
public class RadioUserService extends MongoService<RadioUser> {
	private final static Map<UUID, RadioUser> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	@Override
	public Map<UUID, RadioUser> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
