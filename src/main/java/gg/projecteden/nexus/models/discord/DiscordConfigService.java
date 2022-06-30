package gg.projecteden.nexus.models.discord;

import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.api.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DiscordConfig.class)
public class DiscordConfigService extends MongoService<DiscordConfig> {
	private final static Map<UUID, DiscordConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, DiscordConfig> getCache() {
		return cache;
	}

}
