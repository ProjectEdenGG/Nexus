package me.pugabyte.nexus.models.dumpster;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Dumpster.class)
public class DumpsterService extends MongoService<Dumpster> {
	private final static Map<UUID, Dumpster> cache = new HashMap<>();

	public Map<UUID, Dumpster> getCache() {
		return cache;
	}

	public Dumpster get() {
		return super.get(Nexus.getUUID0());
	}

}
