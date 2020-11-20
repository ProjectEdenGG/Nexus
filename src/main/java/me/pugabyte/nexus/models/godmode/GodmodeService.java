package me.pugabyte.nexus.models.godmode;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Godmode.class)
public class GodmodeService extends MongoService {
	private final static Map<UUID, Godmode> cache = new HashMap<>();

	public Map<UUID, Godmode> getCache() {
		return cache;
	}

	@Override
	public <T> void saveSync(T object) {
		if (!((Godmode) object).isEnabledRaw())
			super.deleteSync(object);
		else
			super.saveSync(object);
	}

}
