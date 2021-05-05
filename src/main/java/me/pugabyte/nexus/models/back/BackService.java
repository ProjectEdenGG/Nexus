package me.pugabyte.nexus.models.back;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static eden.utils.Utils.isNullOrEmpty;

@PlayerClass(Back.class)
public class BackService extends MongoService<Back> {
	private final static Map<UUID, Back> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, Back> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	protected boolean deleteIf(Back back) {
		return isNullOrEmpty(back.getLocations());
	}

}
