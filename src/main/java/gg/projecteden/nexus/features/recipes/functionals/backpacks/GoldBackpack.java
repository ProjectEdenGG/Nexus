package gg.projecteden.nexus.features.recipes.functionals.backpacks;

import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GoldBackpack extends IronBackpack {

	public static ItemStack result = new ItemBuilder(ItemModelType.BACKPACK_3D_GOLD).name("Gold Backpack").build();

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
	public Backpacks.BackpackTier getTier() {
		return Backpacks.BackpackTier.GOLD;
	}


}
