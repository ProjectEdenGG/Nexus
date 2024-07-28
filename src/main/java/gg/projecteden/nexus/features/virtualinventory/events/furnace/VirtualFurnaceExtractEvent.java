package gg.projecteden.nexus.features.virtualinventory.events.furnace;

import gg.projecteden.nexus.features.virtualinventory.events.VirtualInventoryEvent;
import gg.projecteden.nexus.features.virtualinventory.models.inventories.impl.VirtualFurnace;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class VirtualFurnaceExtractEvent extends VirtualInventoryEvent {
	private final Player player;
	private final ItemStack itemStack;
	private final int experience;

	public VirtualFurnaceExtractEvent(VirtualFurnace inventory, Player player, ItemStack itemStack, int experience) {
		super(inventory);
		this.player = player;
		this.itemStack = itemStack;
		this.experience = experience;
	}
}
