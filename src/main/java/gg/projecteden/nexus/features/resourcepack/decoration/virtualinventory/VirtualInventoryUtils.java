package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory;

import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VirtualInventoryUtils {

	public static List<CookingRecipe<?>> getCookingRecipe(ItemStack ingredient) {
		List<CookingRecipe<?>> recipes = new ArrayList<>();
		Iterator<Recipe> iter = Bukkit.recipeIterator();
		while (iter.hasNext()) {
			Recipe recipe = iter.next();
			if (recipe instanceof CookingRecipe cookingRecipe) {
				if (ItemUtils.isFuzzyMatch(cookingRecipe.getInput(), ingredient))
					recipes.add(cookingRecipe);

				if (cookingRecipe.getInputChoice().test(ingredient))
					recipes.add(cookingRecipe);
			}
		}

		return recipes;
	}

	public static @Nullable FurnaceRecipe getFurnaceRecipe(ItemStack ingredient) {
		Iterator<Recipe> iter = Bukkit.recipeIterator();
		while (iter.hasNext()) {
			Recipe recipe = iter.next();
			if (recipe instanceof FurnaceRecipe furnaceRecipe) {
				if (ItemUtils.isFuzzyMatch(furnaceRecipe.getInput(), ingredient))
					return furnaceRecipe;

				if (furnaceRecipe.getInputChoice().test(ingredient))
					return furnaceRecipe;
			}
		}

		return null;
	}
}
