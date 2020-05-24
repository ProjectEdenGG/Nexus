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

import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;

// TODO: Trades & Economy
// TODO: make Collector move to random locations
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
		BAKER(2659) {
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
							.result(new ItemBuilder(Material.POTION).potion(PotionType.POISON, true, false).lore(itemLore))
							.ingredient(new ItemBuilder(Material.STICK).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.WEAKNESS).lore(itemLore))
							.ingredient(new ItemBuilder(Material.STICK).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.SLOWNESS).lore(itemLore))
							.ingredient(new ItemBuilder(Material.STICK).lore(itemLore)));
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
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.STICK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.MELON).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.STICK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.HAY_BLOCK).amount(4).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.STICK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.PUMPKIN).amount(9).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.STICK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.POTATO).amount(16).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.STICK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.BEETROOT).amount(16).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.STICK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.CARROT).amount(16).lore(itemLore)));
				}};
			}
		},
		BREWER(2662) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		COLLECTOR(2750) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		FISHERMAN(2653) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.FISHING_ROD).lore(itemLore))
							.ingredient(new ItemBuilder(Material.STICK).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.STICK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.COD).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.STICK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.SALMON).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.STICK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.TROPICAL_FISH).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.STICK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.PUFFERFISH).lore(itemLore)));
				}};
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
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.IRON_INGOT).lore(itemLore))
							.ingredient(new ItemBuilder(Material.SMOOTH_QUARTZ).name("Purified Marble").lore(itemLore)));
				}};
			}
		},
		PASTRY_CHEF(2654) {
			@Override
			List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.STICK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.CAKE).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.STICK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.COOKIE).lore(itemLore)));
				}};
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
