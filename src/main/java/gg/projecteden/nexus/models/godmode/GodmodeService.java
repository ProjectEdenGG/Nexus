package gg.projecteden.nexus.models.godmode;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Godmode.class)
public class GodmodeService extends MongoPlayerService<Godmode> {
	private final static Map<UUID, Godmode> cache = new ConcurrentHashMap<>();

	public Map<UUID, Godmode> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(Godmode godmode) {
		return !godmode.isEnabled();
	}

}
