package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.events;

import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualInventory;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public class VirtualFurnaceFuelBurnEvent extends VirtualInventoryEvent implements Cancellable {
	@Getter
	private final ItemStack fuel;
	@Getter
	private final int burnTime;
	private boolean cancelled;

	public VirtualFurnaceFuelBurnEvent(VirtualInventory inventory, ItemStack fuel) {
		super(inventory);
		this.fuel = fuel;
		this.burnTime = ItemUtils.getBurnTime(fuel);
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
