package gg.projecteden.nexus.features.events.y2025.halloween25;

import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.IBackpack;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Halloween25CandyBasket implements IBackpack {

	private static final ItemStack ITEM = BackpackTier.HALLOWEEN.builder()
		.name("&5Candy Basket")
		.lore("&6Halloween 2025")
		.build();

	@Override
	public ItemStack getItem() {
		return ITEM;
	}

	public static void handleClose(ItemStack backpack, List<ItemStack> contents) {
		int amount = 0;
		for (ItemStack item : contents)
			if (item != null)
				amount += item.getAmount();

		CandyBasketTier tier = CandyBasketTier.EMPTY;
		if (amount >= 333)
			tier = CandyBasketTier.QUARTER;
		if (amount >= 666)
			tier = CandyBasketTier.HALF;
		if (amount >= 1000)
			tier = CandyBasketTier.FULL;

		new ItemBuilder(backpack, true).model(tier.getModel()).build();
	}

	public enum CandyBasketTier {
		EMPTY,
		QUARTER,
		HALF,
		FULL;

		public String getModel() {
			return ItemModelType.valueOf("CANDY_BASKET_" + name()).getModel();
		}

		public static CandyBasketTier of(ItemStack item) {
			String model = new ItemBuilder(item).model();
			for (CandyBasketTier tier : values())
				if (tier.getModel().equals(model))
					return tier;
			return null;
		}

	}

}
