package me.pugabyte.bncore.models.statuehunt;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(StatueHunt.class)
public class StatueHuntService extends MongoService {

	private final static Map<UUID, StatueHunt> cache = new HashMap<>();

	@Override
	public Map<UUID, StatueHunt> getCache() {
		return cache;
	}
}
