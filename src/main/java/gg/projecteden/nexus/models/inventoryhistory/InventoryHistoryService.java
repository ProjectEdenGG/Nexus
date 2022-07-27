package gg.projecteden.nexus.models.inventoryhistory;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.models.inventoryhistory.InventoryHistory.InventorySnapshot;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(InventoryHistory.class)
public class InventoryHistoryService extends MongoPlayerService<InventoryHistory> {
	private final static Map<UUID, InventoryHistory> cache = new ConcurrentHashMap<>();

	public Map<UUID, InventoryHistory> getCache() {
		return cache;
	}

	@Override
	protected void beforeSave(InventoryHistory history) {
		history.janitor();
		history.getSnapshots().sort(Comparator.comparing(InventorySnapshot::getTimestamp).reversed());
	}

}
