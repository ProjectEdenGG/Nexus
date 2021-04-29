package me.pugabyte.nexus.models.interactioncommand;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(InteractionCommandConfig.class)
public class InteractionCommandConfigService extends MongoService<InteractionCommandConfig> {
	private final static Map<UUID, InteractionCommandConfig> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, InteractionCommandConfig> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public InteractionCommandConfig get() {
		return get(Nexus.getUUID0());
	}

}
