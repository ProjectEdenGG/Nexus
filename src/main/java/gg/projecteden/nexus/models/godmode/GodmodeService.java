package gg.projecteden.nexus.models.godmode;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Godmode.class)
public class GodmodeService extends MongoService<Godmode> {
	private final static Map<UUID, Godmode> cache = new ConcurrentHashMap<>();

	public Map<UUID, Godmode> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(Godmode godmode) {
		return !godmode.isEnabled();
	}

}
