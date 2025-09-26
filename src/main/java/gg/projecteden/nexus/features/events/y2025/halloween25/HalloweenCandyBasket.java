package gg.projecteden.nexus.features.events.y2025.halloween25;

import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HalloweenCandyBasket {

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

		CandyBasketTier itemTier = CandyBasketTier.of(backpack);
		if (itemTier != null)
			if (itemTier.ordinal() >= tier.ordinal())
				return;

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
