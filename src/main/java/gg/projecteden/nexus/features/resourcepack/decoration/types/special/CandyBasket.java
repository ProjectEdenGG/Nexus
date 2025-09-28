package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.events.y2025.halloween25.Halloween25CandyBasket.CandyBasketTier;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.BackpackCommand;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CandyBasket extends Backpack {

	final CandyBasketTier candyBasketTier;

	public CandyBasket(CandyBasketTier candyBasketTier) {
		this.tier = BackpackTier.HALLOWEEN;
		this.candyBasketTier = candyBasketTier;
		this.id = "backpack_3d_" + tier.name().toLowerCase() + "_" + candyBasketTier.name().toLowerCase();
		this.name = "Candy Basket";
		this.material = Material.PAPER;
		this.model = candyBasketTier.getModel();
		this.hitboxes = Hitbox.NONE();
		this.disabledPlacements = PlacementType.FLOOR.getDisabledPlacements();
		this.overrideTabComplete = true;
	}

	@Override
	public ItemStack getItem() {
		ItemStack stack = BackpackCommand.fix(super.getItem());
		stack = tier.apply(stack);
		return new ItemBuilder(stack, true).model(candyBasketTier.getModel()).build();
	}
}
