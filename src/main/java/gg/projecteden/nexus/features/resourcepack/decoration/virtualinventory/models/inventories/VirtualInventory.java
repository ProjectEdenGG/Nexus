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

	public abstract void openInventory(Player player);

	public void tick() {}

	@Override
	public String toString() {
		return "VirtualInv{" + "title=" + title + ", uuid=" + uuid + '}';
	}
}
