package gg.projecteden.nexus.models.job;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(ScheduledJobs.class)
public class ScheduledJobsService extends MongoService<ScheduledJobs> {
	private final static Map<UUID, ScheduledJobs> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, ScheduledJobs> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	protected void beforeSave(ScheduledJobs scheduledJobs) {
		scheduledJobs.janitor();
	}

}
