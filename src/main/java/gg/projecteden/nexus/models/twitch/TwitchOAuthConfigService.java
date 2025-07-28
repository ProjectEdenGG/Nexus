package gg.projecteden.nexus.models.twitch;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(TwitchOAuthConfig.class)
public class TwitchOAuthConfigService extends MongoBukkitService<TwitchOAuthConfig> {
	private final static Map<UUID, TwitchOAuthConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, TwitchOAuthConfig> getCache() {
		return cache;
	}

}
