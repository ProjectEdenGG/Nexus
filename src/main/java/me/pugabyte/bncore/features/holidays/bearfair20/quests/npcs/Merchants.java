package me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs;

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
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.fishing.Loot.*;

// TODO: Trades & Economy
public class Merchants {

	private static ItemBuilder goldNugget = new ItemBuilder(Material.GOLD_NUGGET).lore(itemLore).amount(1);
	private static ItemBuilder goldIngot = new ItemBuilder(Material.GOLD_NUGGET).lore(itemLore).amount(1);
	private static ItemBuilder goldBlock = new ItemBuilder(Material.GOLD_NUGGET).lore(itemLore).amount(1);
	private static ItemBuilder moneyUnit = new ItemBuilder(Material.STICK).name("placeholder").lore(itemLore).amount(1);

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
							.ingredient(moneyUnit));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.WEAKNESS).lore(itemLore))
							.ingredient(moneyUnit));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potion(PotionType.SLOWNESS).lore(itemLore))
							.ingredient(moneyUnit));
				}};
			}
		},
		BLACKSMITH(2656) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>();
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
				return new ArrayList<>();
			}
		},
		COLLECTOR(2750) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		FISHERMAN(2653) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.FISHING_ROD).lore(itemLore))
							.ingredient(goldIngot.amount(2)));
					// Default
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(cod));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(salmon));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(tropicalFish));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(pufferfish));
					// Generic
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(bullhead));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(sturgeon));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(woodskip));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(voidSalmon));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(redSnapper));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(redMullet));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(seaCucumber));
					// Island
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(midnightCarp));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(sunfish));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(tigerTrout));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(glacierfish));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(crimsonfish));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(flathead));
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
							.result(moneyUnit)
							.ingredient(new ItemBuilder(Material.CAKE).lore(itemLore)));
					add(new TradeBuilder()
							.result(moneyUnit)
							.ingredient(new ItemBuilder(Material.COOKIE).lore(itemLore)));
				}};
			}
		},
		SORCERER(2658) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>();
			}
		},
		TRADER(2763) {
			@Override
			public List<TradeBuilder> getTrades() {
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

		public abstract List<TradeBuilder> getTrades();
	}
}
