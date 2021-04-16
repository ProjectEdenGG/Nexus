package me.pugabyte.nexus.models.interactioncommand;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(InteractionCommandConfig.class)
public class InteractionCommandConfigService extends MongoService<InteractionCommandConfig> {
	private final static Map<UUID, InteractionCommandConfig> cache = new HashMap<>();

	public Map<UUID, InteractionCommandConfig> getCache() {
		return cache;
	}

	public InteractionCommandConfig get() {
		return get(Nexus.getUUID0());
	}

}
