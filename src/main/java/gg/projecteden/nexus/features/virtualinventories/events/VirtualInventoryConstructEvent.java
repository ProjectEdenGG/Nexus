package gg.projecteden.nexus.features.virtualinventories.events;

import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualInventory;

public class VirtualInventoryConstructEvent extends VirtualInventoryEvent {

	public VirtualInventoryConstructEvent(VirtualInventory<?> inventory) {
		super(inventory);
	}

}
