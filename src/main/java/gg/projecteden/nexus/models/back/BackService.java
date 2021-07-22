package gg.projecteden.nexus.models.back;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.utils.Utils.isNullOrEmpty;

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
