package me.pugabyte.nexus.models.statusbar;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(StatusBar.class)
public class StatusBarService extends MongoService<StatusBar> {
	private final static Map<UUID, StatusBar> cache = new HashMap<>();

	public Map<UUID, StatusBar> getCache() {
		return cache;
	}

}
