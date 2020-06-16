package me.pugabyte.bncore.models.tip;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Tip.class)
public class TipService extends MongoService {
	private final static Map<UUID, Tip> cache = new HashMap<>();

	public Map<UUID, Tip> getCache() {
		return cache;
	}

}
