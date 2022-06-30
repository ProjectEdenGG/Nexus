package gg.projecteden.nexus.models.alerts;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Alerts.class)
public class AlertsService extends MongoPlayerService<Alerts> {
	private final static Map<UUID, Alerts> cache = new ConcurrentHashMap<>();

	public Map<UUID, Alerts> getCache() {
		return cache;
	}

}
