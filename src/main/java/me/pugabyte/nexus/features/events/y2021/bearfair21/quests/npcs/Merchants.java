package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs;

import eden.utils.Utils;
import lombok.Getter;
import me.pugabyte.nexus.features.events.y2021.bearfair21.Quests;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MerchantBuilder;
import me.pugabyte.nexus.utils.MerchantBuilder.TradeBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;


public class Merchants {

	public static ItemBuilder goldNugget = new ItemBuilder(Material.GOLD_NUGGET);
	public static ItemBuilder goldIngot = new ItemBuilder(Material.GOLD_INGOT);
	public static ItemBuilder goldBlock = new ItemBuilder(Material.GOLD_BLOCK);
	public static ItemBuilder TBD = new ItemBuilder(Material.STICK).name("To Be Determined").amount(1);

	public static void openMerchant(Player player, int id) {
		BFMerchant bfMerchant = BFMerchant.getFromId(id);
		if (bfMerchant == null)
			return;

		List<TradeBuilder> trades = bfMerchant.getTrades(player);
		if (Utils.isNullOrEmpty(trades))
			return;

		new MerchantBuilder(StringUtils.camelCase(bfMerchant.name()))
				.trades(trades)
				.open(player);
	}

	public enum BFMerchant {
		ARTIST(2657) {
			@Override
			public List<TradeBuilder> getTrades(Player player) {
				return null;
			}
		},
		BAKER(2659) {
			@Override
			public List<TradeBuilder> getTrades(Player player) {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
							.ingredient(new ItemBuilder(Material.BREAD).amount(64)));
				}};
			}
		},
		BARTENDER(2655) {
			@Override
			public List<TradeBuilder> getTrades(Player player) {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potionType(PotionType.POISON, true, false))
							.ingredient(goldNugget.clone().amount(3)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potionType(PotionType.WEAKNESS))
							.ingredient(goldNugget.clone().amount(3)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potionType(PotionType.SLOWNESS))
							.ingredient(goldNugget.clone().amount(3)));
				}};
			}
		},
		BLACKSMITH(2656) {
			@Override
			public List<TradeBuilder> getTrades(Player player) {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(16))
							.ingredient(new ItemBuilder(Material.ANVIL)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.COAL_BLOCK).amount(2).build())
							.ingredient(new ItemBuilder(Material.IRON_BLOCK).amount(1).build()));
				}};
			}
		},
		BOTANIST(2661) {
			@Override
			public List<TradeBuilder> getTrades(Player player) {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.MELON).amount(8)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.PUMPKIN).amount(12)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.HAY_BLOCK).amount(16)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.SUGAR_CANE).amount(48)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.BEETROOT).amount(32)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.POTATO).amount(64)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.CARROT).amount(64)));
				}};
			}
		},
		BREWER(2662) {
			@Override
			public List<TradeBuilder> getTrades(Player player) {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
							.ingredient(new ItemBuilder(Material.COCOA_BEANS).amount(12)));
				}};
			}
		},
		COLLECTOR(2750) {
			@Override
			public List<TradeBuilder> getTrades(Player player) {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(Quests.getBackPack(player))
							.ingredient(goldNugget.clone().amount(1)));
					add(new TradeBuilder()
							.result(new ItemStack(Material.ELYTRA))
							.ingredient(goldNugget.clone().amount(1)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.GLISTERING_MELON_SLICE))
							.ingredient(new ItemBuilder(Material.GOLDEN_CARROT)));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
							.ingredient(FishingLoot.TIGER_TROUT.getItem(player)));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
							.ingredient(FishingLoot.GLACIERFISH.getItem(player)));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
							.ingredient(FishingLoot.CRIMSONFISH.getItem(player)));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
							.ingredient(FishingLoot.BLOBFISH.getItem(player)));
				}};
			}
		},
		FISHERMAN(2653) {
			@Override
			public List<TradeBuilder> getTrades(Player player) {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.FISHING_ROD))
							.ingredient(goldIngot.clone().amount(2)));
				}};
			}
		},
		INVENTOR(2660) {
			@Override
			public List<TradeBuilder> getTrades(Player player) {
				return null;
			}
		},
		PASTRY_CHEF(2654) {
			@Override
			public List<TradeBuilder> getTrades(Player player) {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.EGG))
							.ingredient(goldNugget.clone().amount(5)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
							.ingredient(new ItemBuilder(Material.EGG)));
				}};
			}
		},
		SORCERER(2658) {
			@Override
			public List<TradeBuilder> getTrades(Player player) {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(3))
							.ingredient(new ItemBuilder(Material.CAULDRON)));
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

		public abstract List<TradeBuilder> getTrades(Player player);
	}
}
