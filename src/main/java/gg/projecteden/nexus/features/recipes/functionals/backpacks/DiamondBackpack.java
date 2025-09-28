package gg.projecteden.nexus.features.recipes.functionals.backpacks;

import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DiamondBackpack extends GoldBackpack {

	public static ItemStack result = BackpackTier.DIAMOND.builder()
		.name("Diamond Backpack")
		.build();

	@Override
	public ItemStack getItem() {
		return result;
	}

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	public ItemStack getPreviousBackpack() {
		return GoldBackpack.result;
	}

	@Override
	public Material getUpgradeMaterial() {
		return Material.DIAMOND;
	}

	@Override
	public BackpackTier getTier() {
		return BackpackTier.DIAMOND;
	}

}
