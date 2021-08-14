package gg.projecteden.nexus.models.tip;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Tip.class)
public class TipService extends MongoService<Tip> {
	private final static Map<UUID, Tip> cache = new ConcurrentHashMap<>();

	public Map<UUID, Tip> getCache() {
		return cache;
	}

}
