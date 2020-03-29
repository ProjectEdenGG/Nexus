package me.pugabyte.bncore.models.hallofhistory;

import me.pugabyte.bncore.models.MongoService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HallOfHistoryService extends MongoService {
	public static Map<UUID, List<HallOfHistory>> cache = new HashMap<>();

	public List<HallOfHistory> getHistory(UUID uuid) {
		return cache.computeIfAbsent(uuid, $ -> {
			List<HallOfHistory> list;
		});
	}

	public HallOfHistory getByAllArgs(UUID uuid, String rank, String current, LocalDate promotionDate, LocalDate resignationDate) {
		return new HallOfHistory();
	}

	public void delete(UUID uuid, String rank, boolean current) {
		database.where("uuid = ? AND rank = ? AND current = ?", uuid, rank, current).delete();
	}

}
