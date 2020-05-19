package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import lombok.NonNull;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeObject {
	@NonNull
	private ItemStack result;
	@NonNull
	private final List<ItemStack> ingredients = new ArrayList<>();
	@NonNull
	private String key;

	public RecipeObject key(String key) {
		this.key = key;
		return this;
	}

	public RecipeObject result(Material result) {
		this.result = new ItemStack(result);
		return this;
	}

	public RecipeObject result(ItemStack result) {
		this.result = result;
		return this;
	}

	public RecipeObject result(ItemBuilder result) {
		this.result = result.build();
		return this;
	}

	public RecipeObject ingredient(Material ingredient) {
		this.ingredients.add(new ItemStack(ingredient));
		return this;
	}

	public RecipeObject ingredient(ItemStack ingredient) {
		this.ingredients.add(ingredient);
		return this;
	}

	public RecipeObject ingredient(ItemBuilder ingredient) {
		this.ingredients.add(ingredient.build());
		return this;
	}

	public RecipeObject ingredients(List<ItemStack> ingredients) {
		this.ingredients.addAll(ingredients);
		return this;
	}

	public List<ItemStack> getIngredients() {
		return ingredients;
	}

	public ItemStack getResult() {
		return result;
	}

	public String getKey() {
		return key;
	}
}


