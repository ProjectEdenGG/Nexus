package me.pugabyte.nexus.models.changelog;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.changelog.Changelog.ChangelogEntry;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Changelog.class)
public class ChangelogService extends MongoService<Changelog> {
	private final static Map<UUID, Changelog> cache = new HashMap<>();

	public Map<UUID, Changelog> getCache() {
		return cache;
	}

	@Override
	public void saveSync(Changelog changelog) {
		changelog.getEntries().sort(Comparator.comparing(ChangelogEntry::getTimestamp).reversed());
		super.saveSync(changelog);
	}

}
