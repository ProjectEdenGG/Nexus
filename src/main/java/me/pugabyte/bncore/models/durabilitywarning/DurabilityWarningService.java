package me.pugabyte.bncore.models.durabilitywarning;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(DurabilityWarning.class)
public class DurabilityWarningService extends MongoService {
	private final static Map<UUID, DurabilityWarning> cache = new HashMap<>();

	public Map<UUID, DurabilityWarning> getCache() {
		return cache;
	}

	@Override
	public <T> void saveSync(T object) {
		if (((DurabilityWarning) object).isEnabled())
			super.delete(object);
		else
			super.saveSync(object);
	}

}
