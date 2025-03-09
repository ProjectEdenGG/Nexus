package gg.projecteden.nexus.features.virtualinventories.managers;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualInventory;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualInventoryType;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualPersonalInventory;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.virtualinventories.VirtualInventoriesConfigService;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

// Per player inventories for one block - mostly used in events
/* TODO
	- handle breaking blocks
*/
public class VirtualPersonalInventoryManager extends Feature {
	private static int taskId;

	public static <T extends VirtualInventory<?>> T getOrCreate(Location location, Player player, VirtualInventoryType type) {
		var inv = new VirtualInventoriesConfigService().get0().getPersonalInventories()
			.computeIfAbsent(location.getWorld().getName(), $1 -> new HashMap<>())
			.computeIfAbsent(location.getBlockX(), $1 -> new HashMap<>())
			.computeIfAbsent(location.getBlockZ(), $1 -> new HashMap<>())
			.computeIfAbsent(location.getBlockY(), $1 -> new HashMap<>())
			.computeIfAbsent(player.getUniqueId(), $ -> (VirtualInventory<?>) type.getPersonalConstructor().apply(type, location, player));

		return (T) inv;
	}

	@Override
	public void onStart() {
		var service = new VirtualInventoriesConfigService();

		taskId = Tasks.repeat(TickTime.TICK.x(5), TickTime.TICK, () -> {
			if (!VirtualInventoryManager.isTicking())
				return;

			var config = service.get0();

			var processed = config.getPersonalInventories()
				.values()
				.stream()
				.flatMap(map -> map.values().stream())
				.flatMap(map -> map.values().stream())
				.flatMap(map -> map.values().stream())
				.flatMap(map -> map.values().stream())
				.toList()
				.stream()
				.filter(virtualInventory -> {
					if (!(virtualInventory instanceof VirtualPersonalInventory virtualPersonalInventory))
						return true;

					return virtualPersonalInventory.getLocation().isChunkLoaded();
				})
				.anyMatch(VirtualInventory::tick);

			if (processed)
				service.save(config);
		});
	}

	@Override
	public void onStop() {
		var service = new VirtualInventoriesConfigService();
		service.save(service.get0());
	}

}
