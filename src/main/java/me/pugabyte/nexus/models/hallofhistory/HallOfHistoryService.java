package me.pugabyte.nexus.models.hallofhistory;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static eden.utils.Utils.isNullOrEmpty;

@PlayerClass(HallOfHistory.class)
public class HallOfHistoryService extends MongoService<HallOfHistory> {
	public static Map<UUID, HallOfHistory> cache = new HashMap<>();

	public Map<UUID, HallOfHistory> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(HallOfHistory hallOfHistory) {
		return isNullOrEmpty(hallOfHistory.getRankHistory());
	}

}
