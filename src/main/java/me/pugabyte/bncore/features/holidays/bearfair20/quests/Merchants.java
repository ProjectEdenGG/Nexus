package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import lombok.Getter;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MerchantBuilder;
import me.pugabyte.bncore.utils.MerchantBuilder.TradeBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class Merchants {

	public static void openMerchant(Player player, int id) {
		BFMerchant bfMerchant = BFMerchant.getFromId(id);
		if (bfMerchant == null)
			return;
		new MerchantBuilder(StringUtils.camelCase(bfMerchant.name())).trades(bfMerchant.getTrades()).open(player);
	}

	private enum BFMerchant {
		ARTIST(2657) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		BAKER(6667) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		BARTENDER(2655) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.POISON, true, false))
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
		BLACKSMITH(2656) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		BOTANIST(2661) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		BREWER(2662) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		FISHERMAN(2653) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		INVENTOR(2660) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		MINER(2743) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		PASTRY_CHEF(2654) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		SORCERER(2658) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<>();
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

			return null;
		}

		abstract List<TradeBuilder> getTrades();
	}
}
