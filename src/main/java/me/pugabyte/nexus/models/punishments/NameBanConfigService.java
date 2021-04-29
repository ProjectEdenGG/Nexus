package me.pugabyte.nexus.models.punishments;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(NameBanConfig.class)
public class NameBanConfigService extends MongoService<NameBanConfig> {
	private final static Map<UUID, NameBanConfig> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, NameBanConfig> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public NameBanConfig get() {
		return get(Nexus.getUUID0());
	}

}
