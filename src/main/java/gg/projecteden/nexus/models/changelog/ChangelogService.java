package gg.projecteden.nexus.models.changelog;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.models.changelog.Changelog.ChangelogEntry;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Changelog.class)
public class ChangelogService extends MongoPlayerService<Changelog> {
	private final static Map<UUID, Changelog> cache = new ConcurrentHashMap<>();

	public Map<UUID, Changelog> getCache() {
		return cache;
	}

	@Override
	public void beforeSave(Changelog changelog) {
		changelog.getEntries().sort(Comparator.comparing(ChangelogEntry::getTimestamp).reversed());
	}

}
