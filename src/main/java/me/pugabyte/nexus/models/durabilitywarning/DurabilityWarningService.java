package me.pugabyte.nexus.models.durabilitywarning;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(DurabilityWarning.class)
public class DurabilityWarningService extends MongoService<DurabilityWarning> {
	private final static Map<UUID, DurabilityWarning> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, DurabilityWarning> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	protected boolean deleteIf(DurabilityWarning durabilityWarning) {
		return durabilityWarning.isEnabled();
	}

}
