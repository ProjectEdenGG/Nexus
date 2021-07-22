package gg.projecteden.nexus.models.hallofhistory;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.utils.Utils.isNullOrEmpty;

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
