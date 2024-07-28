package gg.projecteden.nexus.features.virtualinventory.models.inventories;

import gg.projecteden.nexus.features.virtualinventory.VirtualInventoryUtils.VirtualInventoryHolder;
import gg.projecteden.nexus.features.virtualinventory.models.properties.VirtualInventoryProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

@Data
@NoArgsConstructor
public abstract class VirtualInventory<T extends VirtualInventoryProperties> {
	protected VirtualInventoryType type;
	protected int tick = 0;

	private transient Inventory inventory;
	protected transient boolean opened = false;

	public T properties() {
		return (T) getType().getProperties();
	}

	public Inventory getInventory() {
		if (inventory == null)
			inventory =  Bukkit.createInventory(
				new VirtualInventoryHolder(this),
				type.getProperties().inventoryType(),
				colorize("Virtual " + camelCase(type.name()))
			);

		return inventory;
	}

	public void openInventory(Player player) {
		opened = true;
		player.openInventory(getInventory());
		updateInventory();
	}

	public void closeInventory() {
		opened = false;
	}

	public abstract void updateInventory();

	public void tick() {
		tick++;
		if (tick > 6000)
			tick = 0;
	}

}
