package gg.projecteden.nexus.models.interactioncommand;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(InteractionCommandConfig.class)
public class InteractionCommandConfigService extends MongoPlayerService<InteractionCommandConfig> {
	private final static Map<UUID, InteractionCommandConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, InteractionCommandConfig> getCache() {
		return cache;
	}

}
