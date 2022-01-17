package gg.projecteden.nexus.features.recipes.functionals;

import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.features.recipes.CustomRecipes.choiceOf;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shapeless;

public class UncraftingShulkerBox extends FunctionalRecipe {

	private MaterialTag getMaterialTag() {
		return MaterialTag.SHULKER_BOXES;
	}

	@Override
	public ItemStack getResult() {
		return new ItemStack(Material.SHULKER_SHELL, 2);
	}

	@Override
	public @NotNull Recipe getRecipe() {
		return shapeless()
			.add(choiceOf(getMaterialTag()))
			.toMake(getResult())
			.extra("uncrafting")
			.getRecipe();
	}

	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		for (ItemStack ingredient : event.getInventory().getMatrix()) {
			if (!getMaterialTag().isTagged(ingredient))
				continue;

			if (new ItemBuilder(ingredient).nonAirShulkerBoxContents().isEmpty())
				continue;

			event.getInventory().setResult(null);
			return;
		}
	}

}
