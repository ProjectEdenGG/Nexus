package me.pugabyte.nexus.models.durabilitywarning;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(DurabilityWarning.class)
public class DurabilityWarningService extends MongoService<DurabilityWarning> {
	private final static Map<UUID, DurabilityWarning> cache = new HashMap<>();

	public Map<UUID, DurabilityWarning> getCache() {
		return cache;
	}

	@Override
	public void saveSync(DurabilityWarning durabilityWarning) {
		if (durabilityWarning.isEnabled())
			super.delete(durabilityWarning);
		else
			super.saveSync(durabilityWarning);
	}

}
