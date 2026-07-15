package gg.projecteden.nexus.models.stattrack;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(StatTrackItem.class)
public class StatTrackItemService extends MongoPlayerService<StatTrackItem> {
	private final static Map<UUID, StatTrackItem> cache = new ConcurrentHashMap<>();

	public Map<UUID, StatTrackItem> getCache() {
		return cache;
	}

}
