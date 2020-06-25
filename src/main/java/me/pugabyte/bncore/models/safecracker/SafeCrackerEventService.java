package me.pugabyte.bncore.models.safecracker;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SafeCrackerEvent.class)
public class SafeCrackerEventService extends MongoService {

	private final static Map<UUID, SafeCrackerEvent> cache = new HashMap<>();

	public Map<UUID, SafeCrackerEvent> getCache() {
		return cache;
	}

	public SafeCrackerEvent get() {
		return super.get(BNCore.getUUID0());
	}

}
