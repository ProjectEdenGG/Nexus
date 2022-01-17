package gg.projecteden.nexus.models.statusbar;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(StatusBar.class)
public class StatusBarService extends MongoPlayerService<StatusBar> {
	private final static Map<UUID, StatusBar> cache = new ConcurrentHashMap<>();

	public Map<UUID, StatusBar> getCache() {
		return cache;
	}

}
