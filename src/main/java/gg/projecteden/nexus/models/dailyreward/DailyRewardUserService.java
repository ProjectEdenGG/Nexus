package gg.projecteden.nexus.models.dailyreward;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.utils.Tasks;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(DailyRewardUser.class)
public class DailyRewardUserService extends MongoService<DailyRewardUser> {
	private final static Map<UUID, DailyRewardUser> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, DailyRewardUser> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	static {
		Tasks.async(() -> new DailyRewardUserService().cacheAll());
	}

	public List<DailyRewardUser> getAllNotEarnedToday() {
		return getCache().values().stream()
			.filter(user -> !user.getCurrentStreak().isEarnedToday())
			.toList();
	}

}
