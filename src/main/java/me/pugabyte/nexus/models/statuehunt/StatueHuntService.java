package me.pugabyte.nexus.models.statuehunt;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(StatueHunt.class)
public class StatueHuntService extends MongoService<StatueHunt> {

	private final static Map<UUID, StatueHunt> cache = new HashMap<>();

	@Override
	public Map<UUID, StatueHunt> getCache() {
		return cache;
	}
}
