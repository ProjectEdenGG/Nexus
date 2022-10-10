package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.events;

import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.VirtualInventory;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public class VirtualFurnaceCookEvent extends VirtualInventoryEvent implements Cancellable {
	@Getter
	private final ItemStack source;
	@Getter
	private final ItemStack result;

	private boolean cancelled;

	public VirtualFurnaceCookEvent(VirtualInventory inventory, ItemStack source, ItemStack result) {
		super(inventory);
		this.source = source;
		this.result = result;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
