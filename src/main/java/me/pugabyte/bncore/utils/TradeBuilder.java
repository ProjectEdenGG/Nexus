package me.pugabyte.bncore.utils;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class TradeBuilder {
	@NonNull
	private ItemStack result;
	@NonNull
	private final List<ItemStack> ingredients = new ArrayList<>();
	private int maxUses = 1;

	public TradeBuilder result(Material result) {
		this.result = new ItemStack(result);
		return this;
	}

	public TradeBuilder result(ItemStack result) {
		this.result = result;
		return this;
	}

	public TradeBuilder result(ItemBuilder result) {
		this.result = result.build();
		return this;
	}

	public TradeBuilder ingredient(Material ingredient) {
		this.ingredients.add(new ItemStack(ingredient));
		return this;
	}

	public TradeBuilder ingredient(ItemStack ingredient) {
		this.ingredients.add(ingredient);
		return this;
	}

	public TradeBuilder ingredient(ItemBuilder ingredient) {
		this.ingredients.add(ingredient.build());
		return this;
	}

	public TradeBuilder ingredients(List<ItemStack> ingredients) {
		this.ingredients.addAll(ingredients);
		return this;
	}

	public TradeBuilder maxUses(int maxUses) {
		this.maxUses = maxUses;
		return this;
	}

	public MerchantRecipe build() {
		MerchantRecipe merchantRecipe = new MerchantRecipe(result, maxUses);
		merchantRecipe.setIngredients(ingredients);
		return merchantRecipe;
	}

}
