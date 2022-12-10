package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

@Getter
@AllArgsConstructor
public abstract class VirtualInventory {
	private final VirtualInventoryType type;
	private final String title;
	private final Inventory inventory;
	private final UUID uuid;

	private int tick = 0;
	@Setter
	private boolean opened = false;

	public VirtualInventory(VirtualInventoryType type, String title, UUID uuid, Inventory inventory) {
		this.type = type;
		this.title = title;
		this.uuid = uuid;
		this.inventory = inventory;
	}

	public void openInventory(Player player) {
		opened = true;
		player.openInventory(inventory);
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

	@Override
	public String toString() {
		return "VirtualInv{" + "title=" + title + ", uuid=" + uuid + '}';
	}
}
