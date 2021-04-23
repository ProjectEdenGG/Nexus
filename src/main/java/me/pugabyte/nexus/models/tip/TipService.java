package me.pugabyte.nexus.models.tip;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Tip.class)
public class TipService extends MongoService<Tip> {
	private final static Map<UUID, Tip> cache = new HashMap<>();

	public Map<UUID, Tip> getCache() {
		return cache;
	}

}
