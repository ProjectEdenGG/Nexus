package me.pugabyte.nexus.models.quests;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(WebConfig.class)
public class WebConfigService extends MongoService<WebConfig> {
	private final static Map<UUID, WebConfig> cache = new HashMap<>();

	public Map<UUID, WebConfig> getCache() {
		return cache;
	}
}
