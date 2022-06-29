package gg.projecteden.nexus.models.jobs;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(JobConfig.class)
public class JobConfigService extends MongoPlayerService<JobConfig> {
	private final static Map<UUID, JobConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, JobConfig> getCache() {
		return cache;
	}

}
