package gg.projecteden.nexus.models.changelog;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.models.changelog.Changelog.ChangelogEntry;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Changelog.class)
public class ChangelogService extends MongoService<Changelog> {
	private final static Map<UUID, Changelog> cache = new ConcurrentHashMap<>();

	public Map<UUID, Changelog> getCache() {
		return cache;
	}

	@Override
	public void beforeSave(Changelog changelog) {
		changelog.getEntries().sort(Comparator.comparing(ChangelogEntry::getTimestamp).reversed());
	}

}
