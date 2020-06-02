package me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs;

import lombok.Getter;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MerchantBuilder;
import me.pugabyte.bncore.utils.MerchantBuilder.TradeBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.fishing.Loot.*;

// TODO: Trades & Economy
public class Merchants {

	private static ItemBuilder goldNugget = new ItemBuilder(Material.GOLD_NUGGET).lore(itemLore).amount(1);
	private static ItemBuilder goldIngot = new ItemBuilder(Material.GOLD_INGOT).lore(itemLore).amount(1);
	private static ItemBuilder goldBlock = new ItemBuilder(Material.GOLD_BLOCK).lore(itemLore).amount(1);
	private static ItemBuilder TBD = new ItemBuilder(Material.STICK).name("To Be Determined").lore(itemLore).amount(1);

	public static void openMerchant(Player player, int id) {
		BFMerchant bfMerchant = BFMerchant.getFromId(id);
		if (bfMerchant == null)
			return;

		new MerchantBuilder(StringUtils.camelCase(bfMerchant.name())).trades(bfMerchant.getTrades()).open(player);
	}

	public enum BFMerchant {
		ARTIST(2657) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		BAKER(2659) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		BARTENDER(2655) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.POISON, true, false).lore(itemLore))
							.ingredient(TBD));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.WEAKNESS).lore(itemLore))
							.ingredient(TBD));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.SLOWNESS).lore(itemLore))
							.ingredient(TBD));
				}};
			}
		},
		BLACKSMITH(2656) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.LEATHER))
							.ingredient(TBD));
					add(new TradeBuilder()
							.result(TBD)
							.ingredient(new ItemBuilder(Material.ANVIL).lore(itemLore)));
				}};
			}
		},
		BOTANIST(2661) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(goldNugget)
							.ingredient(new ItemBuilder(Material.MELON).amount(8).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget)
							.ingredient(new ItemBuilder(Material.PUMPKIN).amount(12).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget)
							.ingredient(new ItemBuilder(Material.HAY_BLOCK).amount(16).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget)
							.ingredient(new ItemBuilder(Material.SUGAR_CANE).amount(48).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget)
							.ingredient(new ItemBuilder(Material.BEETROOT).amount(32).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget)
							.ingredient(new ItemBuilder(Material.POTATO).amount(64).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget)
							.ingredient(new ItemBuilder(Material.CARROT).amount(64).lore(itemLore)));
				}};
			}
		},
		BREWER(2662) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(goldNugget.amount(4))
							.ingredient(new ItemBuilder(Material.COCOA_BEANS).amount(8).lore(itemLore)));
					add(new TradeBuilder()
							.result(TBD)
							.ingredient(new ItemBuilder(Material.HONEYCOMB).amount(9).lore(itemLore)));
				}};
			}
		},
		COLLECTOR(2750) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(TBD)
							.ingredient(new ItemBuilder(Material.GLISTERING_MELON_SLICE).lore(itemLore))
							.ingredient(new ItemBuilder(Material.GOLDEN_CARROT).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.BLUE_ORCHID).name("Rare Flower").lore(itemLore))
							.ingredient(new ItemBuilder(Material.STONE_PICKAXE).name("Ancient Pickaxe").lore(itemLore)));
					add(new TradeBuilder()
							.result(goldBlock)
							.ingredient(tigerTrout));
					add(new TradeBuilder()
							.result(goldBlock)
							.ingredient(glacierfish));
					add(new TradeBuilder()
							.result(goldBlock)
							.ingredient(crimsonfish));
					add(new TradeBuilder()
							.result(goldBlock)
							.ingredient(flathead));
				}};
			}
		},
		FISHERMAN(2653) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.FISHING_ROD).enchant(Enchantment.LURE, 2).lore(itemLore))
							.ingredient(goldIngot.amount(2)));
					// Default
					add(new TradeBuilder()
							.result(goldNugget)
							.ingredient(cod));
					add(new TradeBuilder()
							.result(goldNugget)
							.ingredient(salmon));
					add(new TradeBuilder()
							.result(goldNugget)
							.ingredient(tropicalFish));
					add(new TradeBuilder()
							.result(goldNugget.amount(2))
							.ingredient(pufferfish));
					// Generic
					add(new TradeBuilder()
							.result(goldNugget.amount(2))
							.ingredient(bullhead));
					add(new TradeBuilder()
							.result(goldNugget.amount(2))
							.ingredient(sturgeon));
					add(new TradeBuilder()
							.result(goldNugget.amount(2))
							.ingredient(woodskip));
					add(new TradeBuilder()
							.result(goldNugget.amount(2))
							.ingredient(voidSalmon));
					add(new TradeBuilder()
							.result(goldNugget.amount(2))
							.ingredient(redSnapper));
					add(new TradeBuilder()
							.result(goldNugget.amount(2))
							.ingredient(redMullet));
					// Island
					add(new TradeBuilder()
							.result(goldNugget.amount(3))
							.ingredient(seaCucumber));
					add(new TradeBuilder()
							.result(goldIngot)
							.ingredient(midnightCarp));
					add(new TradeBuilder()
							.result(goldIngot)
							.ingredient(sunfish));
				}};
			}
		},
		INVENTOR(2660) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		MINER(2743) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.IRON_PICKAXE).lore(itemLore))
							.ingredient(goldIngot));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.IRON_INGOT).lore(itemLore))
							.ingredient(new ItemBuilder(Material.SMOOTH_QUARTZ).name("Purified Marble").lore(itemLore)));
				}};
			}
		},
		PASTRY_CHEF(2654) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.GLOBE_BANNER_PATTERN).name("Recipe for: Honey Stroop Wafel").lore("TODO", "", itemLore))
							.ingredient(new ItemBuilder(Material.CAKE).lore(itemLore))
							.ingredient(new ItemBuilder(Material.COOKIE).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.EGG).lore(itemLore))
							.ingredient(TBD));
				}};
			}
		},
		SORCERER(2658) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					// TODO: Determine enchant on book, if at all
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.ENCHANTED_BOOK).lore(itemLore))
							.ingredient(new ItemBuilder(Material.BOOK).lore(itemLore))
							.ingredient(TBD));
					add(new TradeBuilder()
							.result(TBD)
							.ingredient(new ItemBuilder(Material.CAULDRON).lore(itemLore)));
				}};
			}
		},
		TRADER(2763) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(TBD)
							.ingredient(goldBlock));
				}};
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

		public abstract List<TradeBuilder> getTrades();
	}
}
