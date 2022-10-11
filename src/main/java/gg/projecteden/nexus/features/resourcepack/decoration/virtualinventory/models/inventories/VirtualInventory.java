package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@AllArgsConstructor
public abstract class VirtualInventory {
	private final VirtualInventoryType type;
	private final String title;
	private final UUID uuid;
	private int tick;

	public VirtualInventory(VirtualInventoryType type, String title, UUID uuid) {
		this(type, title, uuid, 0);
	}

	public abstract void openInventory(Player player);

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
