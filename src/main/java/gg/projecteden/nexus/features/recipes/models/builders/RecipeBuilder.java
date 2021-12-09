package gg.projecteden.nexus.features.recipes.models.builders;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gg.projecteden.nexus.features.recipes.CustomRecipes.choiceOf;

public abstract class RecipeBuilder<T extends RecipeBuilder<?>> {
	protected ItemStack result;
	protected String id;
	protected String extra;

	public T toMake(Material result) {
		return toMake(new ItemStack(result));
	}

	public T toMake(Material result, int amount) {
		return toMake(new ItemStack(result, amount));
	}

	public T toMake(ItemStack result) {
		this.result = result;
		return (T) this;
	}

	public T id(String id) {
		this.id = id;
		return (T) this;
	}

	public T extra(String extra) {
		this.extra = extra;
		return (T) this;
	}

	@NotNull
	abstract <R extends Recipe> R getRecipe();

	public NexusRecipe build() {
		NexusRecipe recipe = new NexusRecipe(getRecipe());
		CustomRecipes.recipes.add(recipe);
		return recipe;
	}

	@NotNull
	protected String getKey() {
		if (id == null)
			id = CustomRecipes.getItemName(result);

		return id + (extra == null ? "" : "_" + extra);
	}

	@NotNull
	protected NamespacedKey key() {
		return new NamespacedKey(Nexus.getInstance(), "custom_" + getKey());
	}

	public static ShapedBuilder shaped(String... pattern) {
		return new ShapedBuilder(pattern);
	}

	public static ShapelessBuilder shapeless() {
		return new ShapelessBuilder();
	}

	public static SurroundBuilder surround(MaterialTag center) {
		return surround(choiceOf(center));
	}

	public static SurroundBuilder surround(Material center) {
		return surround(choiceOf(center));
	}

	public static SurroundBuilder surround(ItemStack center) {
		return surround(choiceOf(center));
	}

	public static SurroundBuilder surround(List<?> choices) {
		return surround(choiceOf(choices));
	}

	public static SurroundBuilder surround(RecipeChoice center) {
		return new SurroundBuilder(center);
	}

	public static FurnaceBuilder smelt(Material smelt) {
		return new FurnaceBuilder(smelt);
	}

	public static FurnaceBuilder blast(Material smelt) {
		return new BlastFurnaceBuilder(smelt);
	}

}
