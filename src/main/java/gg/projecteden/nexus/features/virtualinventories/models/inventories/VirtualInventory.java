package gg.projecteden.nexus.features.virtualinventories.models.inventories;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.virtualinventories.VirtualInventoryUtils.VirtualInventoryHolder;
import gg.projecteden.nexus.features.virtualinventories.models.properties.VirtualInventoryProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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
				properties().inventoryType(),
				gg.projecteden.nexus.utils.StringUtils.colorize("Personal " + StringUtils.camelCase(type.name()))
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

	public boolean tick() {
		tick++;
		if (tick > 6000)
			tick = 0;

		return false;
	}

}
