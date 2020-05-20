package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.recipes.CustomRecipes;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;

public class Recipes {

	public static List<RecipeObject> recipes = new ArrayList<>();

	public static ShapedRecipe createBearFairRecipe(String customKey, ItemStack outputItem, String[] shape, Ingredient... ingredients) {
		NamespacedKey key = new NamespacedKey(BNCore.getInstance(), "custom_bearfair_" + customKey);
		ShapedRecipe recipe = new ShapedRecipe(key, outputItem);
		recipe.shape(shape);
		for (Ingredient ingredient : ingredients) {
			recipe.setIngredient(ingredient.getCharacter(), ingredient.getItemStack());
		}
		CustomRecipes.recipes.put(key, recipe);
		return recipe;
	}

	public static void loadRecipes() {
		//
		NamespacedKey biscuitKey = new NamespacedKey(BNCore.getInstance(), "custom_bearfair_anzac_biscuit");
		Bukkit.removeRecipe(biscuitKey);
		//

		RecipeObject biscuit = new RecipeObject()
				.key("anzac_biscuit")
				.ingredient(new ItemStack(Material.SUGAR))
				.ingredient(new ItemStack(Material.WHEAT))
				.ingredient(new ItemStack(Material.WHEAT))
				.ingredient(new ItemStack(Material.WHEAT))
				.ingredient(new ItemStack(Material.MILK_BUCKET))
				.ingredient(new ItemStack(Material.MILK_BUCKET))
				.result(new ItemBuilder(Material.COOKIE).lore(itemLore).name("Anzac Biscuit").build());


		CustomRecipes.addRecipe(createBearFairRecipe(biscuit.getKey(),
				new ItemBuilder(Material.COOKIE).name("Anzac Biscuit").lore(itemLore).build(),
				new String[] {"AAA", "MSM", "WWW"},
				new Ingredient("A", new ItemStack(Material.AIR)),
				new Ingredient("S", new ItemStack(Material.SUGAR)),
				new Ingredient("W", new ItemStack(Material.WHEAT)),
				new Ingredient("M", new ItemStack(Material.MILK_BUCKET)))
		);

		recipes.add(biscuit);
	}

	public static ItemStack getRecipe(List<ItemStack> itemStacks) {
		List<ItemStack> ingredients = new ArrayList<>();

		// Remove nulls/air
		for (ItemStack itemStack : itemStacks) {
			if (!Utils.isNullOrAir(itemStack)) {
				String displayName = null;
				if (itemStack.getItemMeta().hasDisplayName())
					displayName = StringUtils.stripColor(itemStack.getItemMeta().getDisplayName());
				Material type = itemStack.getType();

				ItemBuilder basicItemStack = new ItemBuilder(type);
				if (displayName != null)
					basicItemStack.name(displayName);
				ingredients.add(basicItemStack.build());
			}
		}

		for (RecipeObject recipe : recipes) {
			if (isEqual(ingredients, recipe.getIngredients()))
				return recipe.getResult();
		}
		return null;
	}

	public static boolean isEqual(List<ItemStack> items1, List<ItemStack> items2) {
		return getSortedTypeList(items1).equals(getSortedTypeList(items2));
	}

	public static String getSortedTypeList(List<ItemStack> items) {
		return items.stream()
				.map(itemStack -> itemStack.getType().name())
				.sorted()
				.collect(Collectors.joining(","));
	}
}
