package gg.projecteden.nexus.models.statuehunt;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(StatueHunt.class)
public class StatueHuntService extends MongoPlayerService<StatueHunt> {
	private final static Map<UUID, StatueHunt> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, StatueHunt> getCache() {
		return cache;
	}
}
