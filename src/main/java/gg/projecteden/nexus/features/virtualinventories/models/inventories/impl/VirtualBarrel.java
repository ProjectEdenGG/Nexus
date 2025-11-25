package gg.projecteden.nexus.features.virtualinventories.models.inventories.impl;

import dev.morphia.annotations.Converters;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualInventory;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualInventoryType;
import gg.projecteden.nexus.features.virtualinventories.models.properties.impl.BarrelProperties;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@Converters(ItemStackConverter.class)
public class VirtualBarrel extends VirtualInventory<BarrelProperties> {
	private List<ItemStack> items = new ArrayList<>();

	public VirtualBarrel(VirtualInventoryType type) {
		this.type = type;
	}

	@Override
	public void openInventory(Player player) {
		super.openInventory(player);
		Inventory inv = getInventory();
		inv.setContents(items.toArray(ItemStack[]::new));
	}

	@Override
	public void closeInventory() {
		super.closeInventory();
		Inventory inv = getInventory();
		items.clear();
		items.addAll(Arrays.stream(inv.getContents()).toList());
	}

}
