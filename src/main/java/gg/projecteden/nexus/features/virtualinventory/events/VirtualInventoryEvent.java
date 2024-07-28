package gg.projecteden.nexus.features.virtualinventory.events;

import gg.projecteden.nexus.features.virtualinventory.models.inventories.VirtualInventory;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VirtualInventoryEvent extends Event {
	@Getter
	private static final HandlerList handlerList = new HandlerList();

	@Getter
	private final VirtualInventory<?> inventory;

	public VirtualInventoryEvent(VirtualInventory<?> inventory) {
		this.inventory = inventory;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
