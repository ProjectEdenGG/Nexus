package gg.projecteden.nexus.features.virtualinventories.models.inventories;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.virtualinventories.VirtualInventoryUtils.VirtualInventoryHolder;
import gg.projecteden.nexus.features.virtualinventories.models.properties.VirtualInventoryProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@Data
@NoArgsConstructor
public abstract class VirtualInventory<T extends VirtualInventoryProperties> {
	protected VirtualInventoryType type;

	protected transient Inventory inventory;
	protected transient boolean opened = false;

	public T properties() {
		return (T) getType().getProperties();
	}

	public Inventory getInventory() {
		if (inventory == null)
			if (properties().inventoryType() == InventoryType.BARREL)
				inventory =  Bukkit.createInventory(
					new VirtualInventoryHolder(this),
					9 * 6,
					colorize("Personal " + StringUtils.camelCase(type.name()))
				);
			else
				inventory =  Bukkit.createInventory(
					new VirtualInventoryHolder(this),
					properties().inventoryType(),
					colorize("Personal " + StringUtils.camelCase(type.name()))
				);

		return inventory;
	}

	public void openInventory(Player player) {
		opened = true;
		player.openInventory(getInventory());
	}

	public void closeInventory() {
		opened = false;
	}

}
