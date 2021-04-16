package me.pugabyte.nexus.models.hallofhistory;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(HallOfHistory.class)
public class HallOfHistoryService extends MongoService<HallOfHistory> {
	public static Map<UUID, HallOfHistory> cache = new HashMap<>();

	public Map<UUID, HallOfHistory> getCache() {
		return cache;
	}

	public void save(HallOfHistory hallOfHistory) {
		if (hallOfHistory.getRankHistory() == null || hallOfHistory.getRankHistory().size() == 0)
			super.delete(hallOfHistory);
		else
			super.save(hallOfHistory);
	}

}
