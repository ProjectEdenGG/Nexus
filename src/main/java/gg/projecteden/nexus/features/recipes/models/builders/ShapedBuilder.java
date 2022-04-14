package gg.projecteden.nexus.features.recipes.models.builders;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static gg.projecteden.nexus.features.recipes.CustomRecipes.choiceOf;

public class ShapedBuilder extends RecipeBuilder<ShapedBuilder> {
	private final String[] pattern;
	private final Map<Character, RecipeChoice> ingredients = new HashMap<>();

	public ShapedBuilder(String... pattern) {
		this.pattern = pattern;
	}

	@NotNull
	public ShapedBuilder add(char character, @NotNull Material material) {
		return add(character, choiceOf(material));
	}

	@NotNull
	public ShapedBuilder add(char character, @NotNull ItemStack item) {
		return add(character, choiceOf(item));
	}

	@NotNull
	public ShapedBuilder add(char character, @NotNull RecipeChoice ingredient) {
		this.ingredients.put(character, ingredient);
		return this;
	}

	@NotNull
	@Override
	public Recipe getRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(key(), result);

		recipe.shape(pattern);

		ingredients.forEach(recipe::setIngredient);

		return recipe;
	}

}
