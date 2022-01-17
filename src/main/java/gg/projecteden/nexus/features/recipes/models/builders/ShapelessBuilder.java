package gg.projecteden.nexus.features.recipes.models.builders;

import org.bukkit.Material;
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
		while (count-- > 0)
			add(new MaterialChoice(ingredient));
		return this;
	}

	@NotNull
	public ShapelessBuilder add(@NotNull ItemStack item, int count) {
		while (count-- > 0)
			add(new ExactChoice(item));
		return this;
	}

	@NotNull
	public ShapelessBuilder add(@NotNull RecipeChoice ingredient) {
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
