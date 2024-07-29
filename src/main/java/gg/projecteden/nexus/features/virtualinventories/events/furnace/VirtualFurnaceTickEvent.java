package gg.projecteden.nexus.features.virtualinventories.events.furnace;

import gg.projecteden.nexus.features.virtualinventories.events.VirtualInventoryEvent;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualFurnace;

public class VirtualFurnaceTickEvent extends VirtualInventoryEvent {

	public VirtualFurnaceTickEvent(VirtualFurnace inventory) {
		super(inventory);
	}

}
