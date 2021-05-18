package me.pugabyte.nexus.features.recipes;

import me.pugabyte.nexus.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RecipeUtils {

	public static List<List<ItemStack>> uncraft(ItemStack item) {
		List<Recipe> recipes = Bukkit.getRecipesFor(item);
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
		return match(recipe1, recipe2);
	}

	public static boolean areSimilar(Recipe recipe1, Recipe recipe2) {
		if (recipe1 == recipe2)
			return true;
		if (recipe1 == null || recipe2 == null)
			return false;
		return match(recipe1, recipe2);
	}

	private static boolean match(Recipe recipe1, Recipe recipe2) {
		if (((Keyed) recipe1).getKey().toString().equals(((Keyed) recipe2).getKey().toString())) return true;
		if (recipe1 instanceof ShapedRecipe r1) {
			if (!(recipe2 instanceof ShapedRecipe r2))
				return false;

			ItemStack[] matrix1 = shapeToMatrix(r1.getShape(), r1.getIngredientMap());
			ItemStack[] matrix2 = shapeToMatrix(r2.getShape(), r2.getIngredientMap());

			if (!Arrays.equals(matrix1, matrix2)) {
				mirrorMatrix(matrix1);
				return Arrays.equals(matrix1, matrix2);
			}
			return true;
		} else if (recipe1 instanceof ShapelessRecipe) {
			if (!(recipe2 instanceof ShapelessRecipe))
				return false;

			try {
				ShapelessRecipe r1 = (ShapelessRecipe) recipe1;
				ShapelessRecipe r2 = (ShapelessRecipe) recipe2;

				if (r1.getIngredientList().isEmpty() || r2.getIngredientList().isEmpty())
					return false;

				List<ItemStack> find = r1.getIngredientList();
				List<ItemStack> compare = r2.getIngredientList();

				if (find.size() != compare.size())
					return false;

				for (ItemStack item : compare) {
					if (!find.remove(item))
						return false;
				}
				return find.isEmpty();
			} catch (Exception ignore) {
				return false;
			}
		} else if (recipe1 instanceof FurnaceRecipe r1) {
			if (!(recipe2 instanceof FurnaceRecipe r2))
				return false;

			return r1.getInput().getType() == r2.getInput().getType();
		} else return false;
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