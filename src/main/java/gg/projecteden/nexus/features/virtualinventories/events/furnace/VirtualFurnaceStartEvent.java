package gg.projecteden.nexus.features.virtualinventories.events.furnace;

import gg.projecteden.nexus.features.virtualinventories.events.VirtualInventoryEvent;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualFurnace;

public class VirtualFurnaceStartEvent extends VirtualInventoryEvent {

	public VirtualFurnaceStartEvent(VirtualFurnace inventory) {
		super(inventory);
	}

}
