package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class TradeRecipes {

	@Data
	public static class Trade {
		@NonNull
		private ItemStack result;
		@NonNull
		private ItemStack ingredient1;
		private ItemStack ingredient2;
		private int maxUses;

		public Trade(ItemStack result, ItemStack ingredient1) {
			this.result = result;
			this.ingredient1 = ingredient1;
			this.maxUses = Integer.MAX_VALUE;
		}

		public Trade(ItemStack result, ItemStack ingredient1, int maxUses) {
			this.result = result;
			this.ingredient1 = ingredient1;
			this.maxUses = maxUses;
		}

		public Trade(ItemStack result, ItemStack ingredient1, ItemStack ingredient2) {
			this.result = result;
			this.ingredient1 = ingredient1;
			this.ingredient2 = ingredient2;
			this.maxUses = Integer.MAX_VALUE;
		}

		public Trade(ItemStack result, ItemStack ingredient1, ItemStack ingredient2, int maxUses) {
			this.result = result;
			this.ingredient1 = ingredient1;
			this.ingredient2 = ingredient2;
			this.maxUses = maxUses;
		}

		public MerchantRecipe getTrade() {
			MerchantRecipe merchantRecipe = new MerchantRecipe(result, maxUses);
			merchantRecipe.addIngredient(ingredient1);
			if (ingredient2 != null)
				merchantRecipe.addIngredient(ingredient2);
			return merchantRecipe;
		}
	}

	public static void openMerchant(Player player) {
		Merchant merchant = Bukkit.createMerchant("Test Merchant");
		List<MerchantRecipe> recipes;
		recipes = bartenderTrades();
		merchant.setRecipes(recipes);
		player.openMerchant(merchant, true);
	}

	private static List<MerchantRecipe> bartenderTrades() {
		List<MerchantRecipe> recipes = new ArrayList<>();
		recipes.add(new Trade(
				new ItemBuilder(Material.POTION).potion(PotionType.POISON).build(),
				new ItemStack(Material.STICK, 1))
				.getTrade());
		recipes.add(new Trade(
				new ItemBuilder(Material.POTION).potion(PotionType.WEAKNESS).build(),
				new ItemStack(Material.STICK, 1))
				.getTrade());
		recipes.add(new Trade(
				new ItemBuilder(Material.POTION).potion(PotionType.SLOWNESS).build(),
				new ItemStack(Material.STICK, 1))
				.getTrade());
		return recipes;
	}
}
