package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import lombok.Getter;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MerchantBuilder;
import me.pugabyte.bncore.utils.MerchantBuilder.TradeBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class Merchants {

	public static void openMerchant(Player player, int id) {
		BFMerchant bfMerchant = BFMerchant.getFromId(id);
		new MerchantBuilder(bfMerchant.name()).trades(bfMerchant.getTrades()).open(player);
	}

	private enum BFMerchant {
		BLACKSMITH(1234) {
			@Override
			List<TradeBuilder> getTrades() {
				return null;
			}
		},
		BARTENDER(12345) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.POISON))
							.ingredient(Material.STICK));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.WEAKNESS))
							.ingredient(Material.STICK));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.SLOWNESS))
							.ingredient(Material.STICK));
				}};
			}
		},
		BAKER(123456) {
			@Override
			List<TradeBuilder> getTrades() {
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

		abstract List<TradeBuilder> getTrades();
	}
}
