package gg.projecteden.nexus.features.virtualinventory.events.furnace;

import gg.projecteden.nexus.features.virtualinventory.events.VirtualInventoryEvent;
import gg.projecteden.nexus.features.virtualinventory.models.inventories.impl.VirtualFurnace;

public class VirtualFurnaceTickEvent extends VirtualInventoryEvent {

	public VirtualFurnaceTickEvent(VirtualFurnace inventory) {
		super(inventory);
	}

}
