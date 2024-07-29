package gg.projecteden.nexus.features.virtualinventories.models.properties.impl;

import gg.projecteden.nexus.features.virtualinventories.models.properties.VirtualInventoryProperties;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.SmokingRecipe;

@Data
@Accessors(fluent = true, chain = true)
public class FurnaceProperties extends VirtualInventoryProperties {
	private InventoryType inventoryType;
	private Class<? extends CookingRecipe<?>> recipeClass;
	private double cookMultiplier;
	private double fuelMultiplier;

	public static final FurnaceProperties FURNACE = build(InventoryType.FURNACE, FurnaceRecipe.class, 1.0, 1.0);
	public static final FurnaceProperties BLAST_FURNACE = build(InventoryType.FURNACE, BlastingRecipe.class, 2.0, 1.0);
	public static final FurnaceProperties SMOKER = build(InventoryType.FURNACE, SmokingRecipe.class, 2.0, 1.0);

	private static FurnaceProperties build(InventoryType inventoryType, Class<? extends CookingRecipe<?>> recipeClass, double cookX, double fuelX) {
		return new FurnaceProperties()
			.inventoryType(inventoryType)
			.recipeClass(recipeClass)
			.cookMultiplier(cookX)
			.fuelMultiplier(fuelX);
	}

}
