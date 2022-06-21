package gg.projecteden.nexus.models.dailyreward;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.utils.Tasks;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DailyRewardUser.class)
public class DailyRewardUserService extends MongoPlayerService<DailyRewardUser> {
	private final static Map<UUID, DailyRewardUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, DailyRewardUser> getCache() {
		return cache;
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
