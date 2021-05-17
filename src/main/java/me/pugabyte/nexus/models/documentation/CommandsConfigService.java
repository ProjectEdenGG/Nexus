package me.pugabyte.nexus.models.documentation;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(CommandsConfig.class)
public class CommandsConfigService extends MongoService<CommandsConfig> {
	private final static Map<UUID, CommandsConfig> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, CommandsConfig> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public CommandsConfig get() {
		return super.get(Nexus.getUUID0());
	}

}
