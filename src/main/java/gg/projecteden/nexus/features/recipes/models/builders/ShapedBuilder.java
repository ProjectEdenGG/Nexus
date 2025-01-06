package gg.projecteden.nexus.features.recipes.models.builders;

import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ShapedBuilder extends RecipeBuilder<ShapedBuilder> {
	private final String[] pattern;
	private final Map<Character, RecipeChoice> ingredients = new HashMap<>();

	public ShapedBuilder(String... pattern) {
		this.pattern = pattern;
	}

	private long getCount(char character) {
		return Arrays.stream(pattern)
			.map(string -> Arrays.asList(string.split("")))
			.flatMap(Collection::stream)
			.filter(_character -> _character.equals(String.valueOf(character)))
			.count();
	}

	@NotNull
	public ShapedBuilder add(char character, @NotNull Material material) {
		ingredientIds.add(getCount(character) + "_" + material.name());
		return add(character, CustomRecipes.choiceOf(material));
	}

	@NotNull
	public ShapedBuilder add(char character, @NotNull Tag<Material> tag) {
		ingredientIds.add(getCount(character) + "_" + tag.getKey().getKey());
		return add(character, CustomRecipes.choiceOf(tag));
	}

	@NotNull
	public ShapedBuilder add(char character, @NotNull ItemStack item) {
		ingredientIds.add(getCount(character) + "_" + StringUtils.pretty(item));
		return add(character, CustomRecipes.choiceOf(item));
	}

	@NotNull
	public ShapedBuilder add(char character, @NotNull RecipeChoice ingredient) {
		ingredients.put(character, ingredient);
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
