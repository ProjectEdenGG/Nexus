package gg.projecteden.nexus.models.buildcontest;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(BuildContest.class)
public class BuildContestService extends MongoService<BuildContest> {
	private final static Map<UUID, BuildContest> cache = new ConcurrentHashMap<>();

	public Map<UUID, BuildContest> getCache() {
		return cache;
	}

}
