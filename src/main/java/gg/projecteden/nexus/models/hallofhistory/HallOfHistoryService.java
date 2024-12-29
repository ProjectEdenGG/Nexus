package gg.projecteden.nexus.models.hallofhistory;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.utils.Nullables;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(HallOfHistory.class)
public class HallOfHistoryService extends MongoPlayerService<HallOfHistory> {
	private final static Map<UUID, HallOfHistory> cache = new ConcurrentHashMap<>();

	public Map<UUID, HallOfHistory> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(HallOfHistory hallOfHistory) {
		return Nullables.isNullOrEmpty(hallOfHistory.getRankHistory());
	}

}
