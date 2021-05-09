package me.pugabyte.nexus.models.hallofhistory;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static eden.utils.Utils.isNullOrEmpty;

@PlayerClass(HallOfHistory.class)
public class HallOfHistoryService extends MongoService<HallOfHistory> {
	private final static Map<UUID, HallOfHistory> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, HallOfHistory> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	protected boolean deleteIf(HallOfHistory hallOfHistory) {
		return isNullOrEmpty(hallOfHistory.getRankHistory());
	}

}
