package gg.projecteden.nexus.models.tip;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Tip.class)
public class TipService extends MongoPlayerService<Tip> {
	private final static Map<UUID, Tip> cache = new ConcurrentHashMap<>();

	public Map<UUID, Tip> getCache() {
		return cache;
	}

}
