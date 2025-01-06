package gg.projecteden.nexus.features.recipes.functionals.armor.wither;

import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.wither.WitherChallenge;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public class CraftedWitherSkull extends FunctionalRecipe {

	@Getter
	public static final ItemStack item = new ItemBuilder(Material.WITHER_SKELETON_SKULL)
		.name("&bCrafted Wither Skeleton Skull")
		.lore("&7Can be used to craft Wither Armor")
		.build();

	@Override
	public ItemStack getResult() {
		return item;
	}

	@Override
	public @NotNull Recipe getRecipe() {
		return RecipeBuilder.shapeless()
			.add(WitherChallenge.WITHER_FRAGMENT, 4)
			.toMake(getResult())
			.getRecipe();
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!ItemUtils.isFuzzyMatch(event.getItemInHand(), item))
			return;
		if (event.getBlockAgainst().getType() == Material.SOUL_SAND)
			return;

		event.setCancelled(true);
	}

}
