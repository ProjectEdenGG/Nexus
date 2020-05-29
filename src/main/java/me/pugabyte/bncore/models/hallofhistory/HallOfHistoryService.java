package me.pugabyte.bncore.models.hallofhistory;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(HallOfHistory.class)
public class HallOfHistoryService extends MongoService {
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
