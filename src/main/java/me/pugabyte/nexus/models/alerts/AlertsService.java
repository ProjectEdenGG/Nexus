package me.pugabyte.nexus.models.alerts;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Alerts.class)
public class AlertsService extends MongoService<Alerts> {
	private final static Map<UUID, Alerts> cache = new HashMap<>();

	public Map<UUID, Alerts> getCache() {
		return cache;
	}

}
