package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import lombok.Getter;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.TradeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class Merchants {

	public static void openMerchant(Player player) {
		Merchant merchant = Bukkit.createMerchant("Test Merchant");
		List<MerchantRecipe> recipes;
		recipes = BFMerchant.BARTENDER.getTrades();
		merchant.setRecipes(recipes);
		player.openMerchant(merchant, true);
	}

	private enum BFMerchant {
		BLACKSMITH(1234) {
			@Override
			List<MerchantRecipe> getTrades() {
				return null;
			}
		},
		BARTENDER(12345) {
			@Override
			List<MerchantRecipe> getTrades() {
				return new ArrayList<MerchantRecipe>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.POISON))
							.ingredient(Material.STICK)
							.build());
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.WEAKNESS))
							.ingredient(Material.STICK)
							.build());
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.SLOWNESS))
							.ingredient(Material.STICK)
							.build());
				}};
			}
		},
		BAKER(123456) {
			@Override
			List<MerchantRecipe> getTrades() {
				return null;
			}
		};

		@Getter
		private final int npcId;

		BFMerchant(int npcId) {
			this.npcId = npcId;
		}

		public static BFMerchant getFromId(int id) {
			for (BFMerchant merchant : values()) {
				if (merchant.getNpcId() == id)
					return merchant;
			}

			throw new InvalidInputException("Bear Fair merchant not found from NPC Id " + id);
		}

		abstract List<MerchantRecipe> getTrades();
	}
}
