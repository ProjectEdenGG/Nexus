package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.listeners.VirtualInventoryListener;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.FurnaceProperties;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualFurnace;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualInventory;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualInventoryType;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// TODO:
//  - VirtualInv: give exp when take items out
public class VirtualInventoryManager extends Feature {

	private static final Map<UUID, VirtualInventory> inventoryMap = new ConcurrentHashMap<>();
	private static int taskId;
	@Getter
	@Setter
	private static boolean ticking = true;

	@Override
	public void onStart() {
		VirtualTileManager.onStart();
		new VirtualInventoryListener();

		taskId = Tasks.repeat(TickTime.TICK.x(5), TickTime.TICK, () -> {
			if (!ticking)
				return;

			for (VirtualInventory inventory : new ArrayList<>(inventoryMap.values())) {
				inventory.tick();
			}
		});
	}

	@Override
	public void onStop() {
		VirtualTileManager.onStop();
		Tasks.cancel(taskId);
	}

	public static VirtualInventory getInventory(Player player) {
		return inventoryMap.get(player.getUniqueId());
	}

	public static VirtualInventory getOrCreate(Player player, VirtualInventoryType type, String title) {
		VirtualInventory virtualInventory = VirtualInventoryManager.getInventory(player);
		if (virtualInventory == null) {
			virtualInventory = VirtualInventoryManager.create(type, player, title);
		}

		return virtualInventory;
	}

	public static VirtualInventory create(VirtualInventoryType type, Player player, String title) {
		VirtualInventory inventory;
		switch (type) {
			case FURNACE -> inventory = new VirtualFurnace(title, (FurnaceProperties) type.getProperties());
			default -> {
				return null;
			}
		}

		inventoryMap.put(player.getUniqueId(), inventory);
		return inventory;
	}
}
