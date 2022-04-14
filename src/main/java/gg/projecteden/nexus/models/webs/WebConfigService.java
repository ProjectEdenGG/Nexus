package gg.projecteden.nexus.models.webs;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(WebConfig.class)
public class WebConfigService extends MongoPlayerService<WebConfig> {
	private final static Map<UUID, WebConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, WebConfig> getCache() {
		return cache;
	}
}
