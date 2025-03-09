package gg.projecteden.nexus.features.virtualinventories.managers;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.virtualinventories.listeners.VirtualInventoryListener;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualInventory;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualInventoryType;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.virtualinventories.VirtualInventoriesConfigService;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

// Inventories not tied to blocks - mostly used for /workbenches
public class VirtualInventoryManager extends Feature {
	private static int taskId;
	@Getter
	@Setter
	private static boolean ticking = true;

	@Override
	public void onStart() {
		new VirtualInventoryListener();

		final var service = new VirtualInventoriesConfigService();
		taskId = Tasks.repeat(TickTime.TICK.x(5), TickTime.TICK, () -> {
			if (!ticking)
				return;

			var config = service.get0();
			if (config.getVirtualInventories().isEmpty())
				return;

			var processed = false;
			for (var inventory : config.getVirtualInventories().values())
				if (inventory.tick())
					processed = true;

			if (processed)
				service.save(config);
		});
	}

	@Override
	public void onStop() {
		Tasks.cancel(taskId);
	}

	public static Map<UUID, VirtualInventory<?>> getVirtualInventories() {
		return new VirtualInventoriesConfigService().get0().getVirtualInventories();
	}

	public static VirtualInventory<?> getInventory(Player player) {
		return getVirtualInventories().get(player.getUniqueId());
	}

	public static <T extends VirtualInventory<?>> T getOrCreate(Player player, VirtualInventoryType type) {
		var inv = getVirtualInventories().computeIfAbsent(player.getUniqueId(), $ -> type.getSharedConstructor().apply(type));
		return (T) inv;
	}

	public static void destroy(VirtualInventory<?> inventory, @Nullable Player player) {
		if (player != null) {
			player.closeInventory();
			getVirtualInventories().remove(player.getUniqueId());
		}

		inventory.closeInventory();
	}
}
