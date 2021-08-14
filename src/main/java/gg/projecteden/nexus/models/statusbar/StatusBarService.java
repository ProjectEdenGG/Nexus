package gg.projecteden.nexus.models.statusbar;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(StatusBar.class)
public class StatusBarService extends MongoService<StatusBar> {
	private final static Map<UUID, StatusBar> cache = new ConcurrentHashMap<>();

	public Map<UUID, StatusBar> getCache() {
		return cache;
	}

}
