package gg.projecteden.nexus.features.recipes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeUtils {

	public static List<List<ItemStack>> uncraft(ItemStack result) {
		List<Recipe> recipes = Bukkit.getRecipesFor(result).stream().filter(recipe -> ItemUtils.isModelMatch(result, recipe.getResult())).toList();
		List<List<ItemStack>> ingredients = new ArrayList<>();
		for (Recipe recipe : recipes) {
			List<ItemStack> _ingredients = new ArrayList<>();
			if (recipe instanceof ShapedRecipe shapedRecipe)
				ItemUtils.combine(_ingredients, new ArrayList<>(shapedRecipe.getIngredientMap().values()));
			else if (recipe instanceof ShapelessRecipe shapelessRecipe)
				ItemUtils.combine(_ingredients, shapelessRecipe.getIngredientList());

			if (!_ingredients.isEmpty())
				ingredients.add(_ingredients);
		}
		return ingredients;
	}

	public static boolean areEqual(Recipe recipe1, Recipe recipe2) {
		if (recipe1 == recipe2)
			return true;
		if (recipe1 == null || recipe2 == null)
			return false;
		if (!recipe1.getResult().equals(recipe2.getResult()))
			return false;

		final NamespacedKey key1 = ((Keyed) recipe1).getKey();
		final NamespacedKey key2 = ((Keyed) recipe2).getKey();
		if (key1.toString().equals(key2.toString()))
			return true;

		final boolean match = match(recipe1, recipe2);
		if (match && !key1.equals(key2))
			Nexus.warn("[Custom Recipes] %s == %s".formatted(key1, key2));

		return match;
	}

	public static boolean areSimilar(Recipe recipe1, Recipe recipe2) {
		if (recipe1 == recipe2)
			return true;
		if (recipe1 == null || recipe2 == null)
			return false;
		return match(recipe1, recipe2);
	}

	private static boolean match(Recipe recipe1, Recipe recipe2) {
		if (recipe1 instanceof ShapedRecipe r1) {
			if (!(recipe2 instanceof ShapedRecipe r2))
				return false;

			ItemStack[] matrix1 = shapeToMatrix(r1.getShape(), r1.getIngredientMap());
			ItemStack[] matrix2 = shapeToMatrix(r2.getShape(), r2.getIngredientMap());

			if (!match(matrix1, matrix2)) {
				mirrorMatrix(matrix1);
				return match(matrix1, matrix2);
			}

			return true;
		} else if (recipe1 instanceof ShapelessRecipe r1) {
			if (!(recipe2 instanceof ShapelessRecipe r2))
				return false;

			try {
				if (r1.getIngredientList().isEmpty() || r2.getIngredientList().isEmpty())
					return false;

				List<ItemStack> find = r1.getIngredientList();
				List<ItemStack> compare = r2.getIngredientList();

				if (find.size() != compare.size())
					return false;

				for (ItemStack item : compare)
					if (!find.remove(item))
						return false;

				return find.isEmpty();
			} catch (Exception ignore) {
				return false;
			}
		} else if (recipe1 instanceof FurnaceRecipe r1) {
			if (!(recipe2 instanceof FurnaceRecipe r2))
				return false;

			return r1.getInput().getType() == r2.getInput().getType();
		} else
			return false;
	}

	private static boolean match(ItemStack[] matrix1, ItemStack[] matrix2) {
		for (int i = 0; i < matrix1.length; i++) {
			final ItemStack i1 = matrix1[i];
			final ItemStack i2 = matrix2[i];

			if (i1 == null) {
				if (i2 != null)
					return false;
			} else {
				if (!i1.equals(i2) || !i1.isSimilar(i2))
					return false;
			}
		}

		return true;
	}

	private static ItemStack[] shapeToMatrix(String[] shape, Map<Character, ItemStack> map) {
		ItemStack[] matrix = new ItemStack[9];
		int slot = 0;

		for (int r = 0; r < shape.length; r++) {
			for (char col : shape[r].toCharArray()) {
				matrix[slot] = map.get(col);
				slot++;
			}
			slot = ((r + 1) * 3);
		}
		return matrix;
	}

	private static void mirrorMatrix(ItemStack[] matrix) {
		ItemStack tmp;
		for (int r = 0; r < 3; r++) {
			tmp = matrix[(r * 3)];
			matrix[(r * 3)] = matrix[(r * 3) + 2];
			matrix[(r * 3) + 2] = tmp;
		}
	}
}
