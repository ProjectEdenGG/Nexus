package gg.projecteden.nexus.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

public class Nullables extends gg.projecteden.api.common.utils.Nullables {

	/**
	 * Tests if an item is not null or {@link MaterialTag#ALL_AIR air}
	 * @param itemStack item
	 * @return if item is not null or air
	 */
	// useful for streams
	@Contract("null -> false; !null -> _")
	public static boolean isNotNullOrAir(ItemStack itemStack) {
		return !isNullOrAir(itemStack);
	}

	/**
	 * Tests if an item is not null or {@link MaterialTag#ALL_AIR air}
	 * @param material item
	 * @return if item is not null or air
	 */
	// useful for streams
	@Contract("null -> false; !null -> _")
	public static boolean isNotNullOrAir(Material material) {
		return !isNullOrAir(material);
	}

	/**
	 * Tests if an item is null or {@link MaterialTag#ALL_AIR air}
	 * @param itemStack item
	 * @return if item is null or air
	 */
	@Contract("null -> true; !null -> _")
	public static boolean isNullOrAir(ItemStack itemStack) {
		return itemStack == null || itemStack.getType().isEmpty();
	}

	/**
	 * Tests if an item is null or {@link MaterialTag#ALL_AIR air}
	 * @param material item
	 * @return if item is null or air
	 */
	@Contract("null -> true; !null -> _")
	public static boolean isNullOrAir(Material material) {
		return material == null || material.isEmpty();
	}

	@Contract("null -> true; !null -> _")
	public static boolean isNullOrAir(Block block) {
		return block == null || block.getType().equals(Material.AIR);
	}

}
