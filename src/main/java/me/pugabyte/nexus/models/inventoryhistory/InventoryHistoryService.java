package me.pugabyte.nexus.models.inventoryhistory;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.inventoryhistory.InventoryHistory.InventorySnapshot;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(InventoryHistory.class)
public class InventoryHistoryService extends MongoService<InventoryHistory> {
	private final static Map<UUID, InventoryHistory> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, InventoryHistory> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	public void saveSync(InventoryHistory history) {
		history.getSnapshots().sort(Comparator.comparing(InventorySnapshot::getTimestamp).reversed());
		super.saveSync(history);
	}

}
