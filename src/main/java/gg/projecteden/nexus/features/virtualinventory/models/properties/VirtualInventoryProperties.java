package gg.projecteden.nexus.features.virtualinventory.models.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.event.inventory.InventoryType;

@Data
@Accessors(fluent = true, chain = true)
public abstract class VirtualInventoryProperties {
	public abstract InventoryType inventoryType();
}
