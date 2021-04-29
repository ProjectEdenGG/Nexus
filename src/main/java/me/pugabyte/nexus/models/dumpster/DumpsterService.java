package me.pugabyte.nexus.models.dumpster;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Dumpster.class)
public class DumpsterService extends MongoService<Dumpster> {
	private final static Map<UUID, Dumpster> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, Dumpster> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public Dumpster get() {
		return super.get(Nexus.getUUID0());
	}

}
