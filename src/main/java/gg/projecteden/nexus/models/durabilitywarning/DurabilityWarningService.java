package gg.projecteden.nexus.models.durabilitywarning;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(DurabilityWarning.class)
public class DurabilityWarningService extends MongoService<DurabilityWarning> {
	private final static Map<UUID, DurabilityWarning> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

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
