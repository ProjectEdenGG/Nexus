package me.pugabyte.nexus.models.changelog;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.changelog.Changelog.ChangelogEntry;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Changelog.class)
public class ChangelogService extends MongoService<Changelog> {
	private final static Map<UUID, Changelog> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, Changelog> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	public void beforeSave(Changelog changelog) {
		changelog.getEntries().sort(Comparator.comparing(ChangelogEntry::getTimestamp).reversed());
	}

}
