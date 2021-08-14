package gg.projecteden.nexus.models.quests;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(WebConfig.class)
public class WebConfigService extends MongoService<WebConfig> {
	private final static Map<UUID, WebConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, WebConfig> getCache() {
		return cache;
	}
}
