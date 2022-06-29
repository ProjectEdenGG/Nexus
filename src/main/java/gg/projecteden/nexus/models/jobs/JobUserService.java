package gg.projecteden.nexus.models.jobs;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(JobUser.class)
public class JobUserService extends MongoPlayerService<JobUser> {
	private final static Map<UUID, JobUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, JobUser> getCache() {
		return cache;
	}

}
