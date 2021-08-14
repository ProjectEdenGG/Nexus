package gg.projecteden.nexus.models.dumpster;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Dumpster.class)
public class DumpsterService extends MongoService<Dumpster> {
	private final static Map<UUID, Dumpster> cache = new ConcurrentHashMap<>();

	public Map<UUID, Dumpster> getCache() {
		return cache;
	}

}
