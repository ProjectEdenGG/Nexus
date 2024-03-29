package gg.projecteden.nexus.models.buildcontest;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(BuildContest.class)
public class BuildContestService extends MongoPlayerService<BuildContest> {
	private final static Map<UUID, BuildContest> cache = new ConcurrentHashMap<>();

	public Map<UUID, BuildContest> getCache() {
		return cache;
	}

}
