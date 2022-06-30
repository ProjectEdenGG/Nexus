package gg.projecteden.nexus.models.dailyvotereward;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DailyVoteReward.class)
public class DailyVoteRewardService extends MongoPlayerService<DailyVoteReward> {
	private final static Map<UUID, DailyVoteReward> cache = new ConcurrentHashMap<>();

	public Map<UUID, DailyVoteReward> getCache() {
		return cache;
	}

}
