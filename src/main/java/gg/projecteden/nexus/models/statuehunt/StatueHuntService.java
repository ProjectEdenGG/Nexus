package gg.projecteden.nexus.models.statuehunt;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(StatueHunt.class)
public class StatueHuntService extends MongoService<StatueHunt> {
	private final static Map<UUID, StatueHunt> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, StatueHunt> getCache() {
		return cache;
	}
}
