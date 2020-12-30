package me.pugabyte.nexus.models.inventoryhistory;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.inventoryhistory.InventoryHistory.InventorySnapshot;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(InventoryHistory.class)
public class InventoryHistoryService extends MongoService {
	private final static Map<UUID, InventoryHistory> cache = new HashMap<>();

	public Map<UUID, InventoryHistory> getCache() {
		return cache;
	}

	@Override
	public <T> void saveSync(T object) {
		InventoryHistory history = (InventoryHistory) object;
		history.getSnapshots().sort(Comparator.comparing(InventorySnapshot::getTimestamp).reversed());
		super.saveSync(object);
	}

}
