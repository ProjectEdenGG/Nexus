package me.pugabyte.nexus.models.godmode;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Godmode.class)
public class GodmodeService extends MongoService<Godmode> {
	private final static Map<UUID, Godmode> cache = new HashMap<>();

	public Map<UUID, Godmode> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(Godmode godmode) {
		return !godmode.isEnabledRaw();
	}

}
