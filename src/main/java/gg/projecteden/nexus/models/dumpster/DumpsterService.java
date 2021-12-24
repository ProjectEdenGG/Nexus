package gg.projecteden.nexus.models.dumpster;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Dumpster.class)
public class DumpsterService extends MongoPlayerService<Dumpster> {
	private final static Map<UUID, Dumpster> cache = new ConcurrentHashMap<>();

	public Map<UUID, Dumpster> getCache() {
		return cache;
	}

}
