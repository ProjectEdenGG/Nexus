package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum Pugmas25Trunk {
	DIAMOND(ItemModelType.EVENT_TRUNK_DIAMOND, Map.of(
		new ItemBuilder(Material.DIAMOND).build(), 5d,
		new ItemBuilder(Material.EMERALD).build(), 5d,
		new ItemBuilder(Material.NETHERITE_INGOT).build(), 5d
	)),
	GOLD(ItemModelType.EVENT_TRUNK_GOLDEN, Map.of(
		new ItemBuilder(Material.GOLD_INGOT).build(), 5d,
		new ItemBuilder(Material.LAPIS_LAZULI).build(), 5d,
		new ItemBuilder(Material.COPPER_ORE).build(), 5d
	)),
	IRON(ItemModelType.EVENT_TRUNK_IRON, Map.of(
		new ItemBuilder(Material.IRON_INGOT).build(), 5d,
		new ItemBuilder(Material.COAL).build(), 5d,
		new ItemBuilder(Material.REDSTONE).build(), 5d
	)),
	;

	private final ItemModelType model;
	private final Map<ItemStack, Double> items;

	public static Pugmas25Trunk of(ItemModelType model) {
		for (Pugmas25Trunk trunk : values())
			if (trunk.getModel().equals(model))
				return trunk;
		return null;
	}
}
