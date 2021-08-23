package gg.projecteden.nexus.features.recipes.functionals;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class UncraftingShulkerBox extends FunctionalRecipe {

	private MaterialTag getMaterialTag() {
		return MaterialTag.SHULKER_BOXES;
	}

	@Override
	public ItemStack getResult() {
		return new ItemStack(Material.SHULKER_SHELL, 2);
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), stripColor("custom_uncrafting_shulker_shell"));
		ShapelessRecipe recipe = new ShapelessRecipe(key, getResult());
		recipe.addIngredient(getMaterialChoice());
		return recipe;
	}

	@Override
	public List<ItemStack> getIngredients() {
		return Collections.emptyList();
	}

	@Override
	public String[] getPattern() {
		return null;
	}

	@Override
	public RecipeChoice.MaterialChoice getMaterialChoice() {
		return new RecipeChoice.MaterialChoice(new ArrayList<>(getMaterialTag().getValues()));
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
