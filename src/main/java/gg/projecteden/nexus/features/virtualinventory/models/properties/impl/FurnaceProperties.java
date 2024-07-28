package gg.projecteden.nexus.features.virtualinventory.models.properties.impl;

import gg.projecteden.nexus.features.virtualinventory.models.properties.VirtualInventoryProperties;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.event.inventory.InventoryType;

@Data
@Accessors(fluent = true, chain = true)
public class FurnaceProperties extends VirtualInventoryProperties {
	private InventoryType inventoryType;
	private double cookMultiplier;
	private double fuelMultiplier;

	public static final FurnaceProperties FURNACE = build(InventoryType.FURNACE, 1.0, 1.0);
	public static final FurnaceProperties BLAST_FURNACE = build(InventoryType.BLAST_FURNACE, 2.0, 1.0);
	public static final FurnaceProperties SMOKER = build(InventoryType.SMOKER, 2.0, 1.0);

	private static FurnaceProperties build(InventoryType inventoryType, double cookX, double fuelX) {
		return new FurnaceProperties().inventoryType(inventoryType).cookMultiplier(cookX).fuelMultiplier(fuelX);
	}

}
