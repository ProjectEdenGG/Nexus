package gg.projecteden.nexus.features.recipes.functionals.backpacks;

import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GoldBackpack extends IronBackpack implements IBackpack {

	public static ItemStack result = BackpackTier.GOLD.builder()
		.name("Gold Backpack")
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
		return IronBackpack.result;
	}

	@Override
	public Material getUpgradeMaterial() {
		return Material.GOLD_INGOT;
	}

	@Override
	public BackpackTier getTier() {
		return BackpackTier.GOLD;
	}


}
