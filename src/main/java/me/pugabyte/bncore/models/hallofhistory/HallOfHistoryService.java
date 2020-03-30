package me.pugabyte.bncore.models.hallofhistory;

import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HallOfHistoryService extends MongoService {
	public static Map<UUID, HallOfHistory> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	public HallOfHistory get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			HallOfHistory history = database.createQuery(HallOfHistory.class).field(_id).equal(uuid).first();
			if (history == null)
				return new HallOfHistory(uuid);
			return history;
		});

		return cache.get(uuid);
	}

	public void save(HallOfHistory hallOfHistory) {
		if (hallOfHistory.getRankHistory() == null || hallOfHistory.getRankHistory().size() == 0)
			super.delete(hallOfHistory);
		else
			super.save(hallOfHistory);
	}

}
