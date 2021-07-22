package gg.projecteden.nexus.models.interactioncommand;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(InteractionCommandConfig.class)
public class InteractionCommandConfigService extends MongoService<InteractionCommandConfig> {
	private final static Map<UUID, InteractionCommandConfig> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, InteractionCommandConfig> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
