package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.events;

import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualInventory;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class VirtualFurnaceExtractEvent extends VirtualInventoryEvent {
	private final Player player;
	private final ItemStack itemStack;
	private final int experience;

	public VirtualFurnaceExtractEvent(VirtualInventory inventory, Player player, ItemStack itemStack, int experience) {
		super(inventory);
		this.player = player;
		this.itemStack = itemStack;
		this.experience = experience;
	}
}
