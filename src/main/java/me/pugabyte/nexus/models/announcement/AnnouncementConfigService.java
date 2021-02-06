package me.pugabyte.nexus.models.announcement;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AnnouncementConfig.class)
public class AnnouncementConfigService extends MongoService {
	private final static Map<UUID, AnnouncementConfig> cache = new HashMap<>();

	public Map<UUID, AnnouncementConfig> getCache() {
		return cache;
	}

}
