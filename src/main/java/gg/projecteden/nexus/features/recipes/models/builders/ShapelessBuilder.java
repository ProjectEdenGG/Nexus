package gg.projecteden.nexus.features.recipes.models.builders;

import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelInstance;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShapelessBuilder extends RecipeBuilder<ShapelessBuilder> {
	private final List<RecipeChoice> ingredients = new ArrayList<>();

	@NotNull
	public ShapelessBuilder add(@NotNull Material... ingredients) {
		for (Material material : ingredients)
			add(material, 1);
		return this;
	}

	@NotNull
	public ShapelessBuilder add(@NotNull ItemStack... items) {
		for (ItemStack item : items)
			add(item, item.getAmount());
		return this;
	}

	@NotNull
	public ShapelessBuilder add(@NotNull Material ingredient, int count) {
		ingredientIds.add(CustomRecipes.keyOf(new ItemStack(ingredient, count)));
		while (count-- > 0)
			add(new MaterialChoice(ingredient));
		return this;
	}

	@NotNull
	public ShapelessBuilder add(@NotNull ItemStack item, int count) {
		ingredientIds.add(CustomRecipes.keyOf(item, count));
		while (count-- > 0)
			add(new ExactChoice(item));
		return this;
	}

	@NotNull
	public ShapelessBuilder add(@NotNull ItemModelInstance... items) {
		for (ItemModelInstance item : items) {
			ingredientIds.add(CustomRecipes.keyOf(item));
			add(new ExactChoice(item.getItem()));
		}
		return this;
	}

	@NotNull
	public ShapelessBuilder add(@NotNull Tag<Material> tag) {
		ingredientIds.add(CustomRecipes.keyOf(tag));
		return add(CustomRecipes.choiceOf(tag));
	}

	@NotNull
	protected ShapelessBuilder add(@NotNull RecipeChoice ingredient) {
		this.ingredients.add(ingredient);
		return this;
	}

	@NotNull
	@Override
	public Recipe getRecipe() {
		ShapelessRecipe recipe = new ShapelessRecipe(key(), result);

		for (RecipeChoice ingredient : ingredients)
			recipe.addIngredient(ingredient);

		return recipe;
	}

}
