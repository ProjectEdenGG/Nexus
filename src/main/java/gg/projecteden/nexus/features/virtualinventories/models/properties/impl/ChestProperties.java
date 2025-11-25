package gg.projecteden.nexus.features.virtualinventories.models.properties.impl;

import gg.projecteden.nexus.features.virtualinventories.models.properties.VirtualInventoryProperties;
import org.bukkit.event.inventory.InventoryType;

public class ChestProperties extends VirtualInventoryProperties {

	@Override
	public InventoryType inventoryType() {
		return InventoryType.CHEST;
	}

}
