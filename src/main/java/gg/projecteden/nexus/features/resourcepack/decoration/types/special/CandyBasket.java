package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.events.y2025.halloween25.HalloweenCandyBasket.CandyBasketTier;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.FixBackpackCommand;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CandyBasket extends Backpack {

	@Getter
	final BackpackTier tier;
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
		ItemStack stack = FixBackpackCommand.fix(super.getItem());
		stack = Backpacks.setTier(stack, tier);
		return new ItemBuilder(stack, true).model(candyBasketTier.getModel()).build();
	}
}
