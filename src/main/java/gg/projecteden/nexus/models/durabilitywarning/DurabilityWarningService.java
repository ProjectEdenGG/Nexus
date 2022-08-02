package gg.projecteden.nexus.models.durabilitywarning;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DurabilityWarning.class)
public class DurabilityWarningService extends MongoPlayerService<DurabilityWarning> {
	private final static Map<UUID, DurabilityWarning> cache = new ConcurrentHashMap<>();

	public Map<UUID, DurabilityWarning> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(DurabilityWarning durabilityWarning) {
		return durabilityWarning.isEnabled();
	}

}
