package me.pugabyte.nexus.models.inventorytracker;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(InventoryTracker.class)
public class InventoryTrackerService extends MongoService {
	private final static Map<UUID, InventoryTracker> cache = new HashMap<>();

	public Map<UUID, InventoryTracker> getCache() {
		return cache;
	}

}
