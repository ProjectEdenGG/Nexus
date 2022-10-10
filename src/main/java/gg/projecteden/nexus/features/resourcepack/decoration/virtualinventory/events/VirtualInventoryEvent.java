package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.events;

import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.VirtualInventory;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VirtualInventoryEvent extends Event {
	private static final HandlerList handlerList = new HandlerList();

	private final VirtualInventory inventory;

	public VirtualInventoryEvent(VirtualInventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}
}
