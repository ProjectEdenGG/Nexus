package gg.projecteden.nexus.utils;

import gg.projecteden.parchment.HasPlayer;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class MerchantBuilder {
	private final Merchant merchant;
	private final List<MerchantRecipe> trades = new ArrayList<>();

	public MerchantBuilder(String name) {
		merchant = Bukkit.createMerchant(name);
	}

	public MerchantBuilder trade(TradeBuilder tradeBuilder) {
		this.trades.add(tradeBuilder.build());
		return this;
	}

	public MerchantBuilder trades(List<TradeBuilder> tradeBuilders) {
		tradeBuilders.forEach(tradeBuilder -> trades.add(tradeBuilder.build()));
		return this;
	}

	public Merchant build() {
		this.merchant.setRecipes(trades);
		return merchant;
	}

	public void open(HasPlayer player) {
		player.getPlayer().openMerchant(build(), true);
	}

	public static class TradeBuilder {
		@Getter
		private ItemStack result;
		@NonNull
		@Getter
		private final List<ItemStack> ingredients = new ArrayList<>();
		private int maxUses = 3000;

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

		public TradeBuilder ingredient(Material ingredient, int amount) {
			this.ingredients.add(new ItemStack(ingredient, amount));
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

}
