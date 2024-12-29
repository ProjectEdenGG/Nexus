package gg.projecteden.nexus.features.events.y2020.bearfair20.quests.npcs;

import gg.projecteden.nexus.features.events.y2020.bearfair20.islands.MainIsland;
import gg.projecteden.nexus.features.events.y2020.bearfair20.islands.MinigameNightIsland;
import gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests;
import gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MerchantBuilder;
import gg.projecteden.nexus.utils.MerchantBuilder.TradeBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

// TODO: Trades & Economy
public class Merchants {

	public static ItemBuilder goldNugget = new ItemBuilder(Material.GOLD_NUGGET).lore(BFQuests.itemLore);
	public static ItemBuilder goldIngot = new ItemBuilder(Material.GOLD_INGOT).lore(BFQuests.itemLore);
	public static ItemBuilder goldBlock = new ItemBuilder(Material.GOLD_BLOCK).lore(BFQuests.itemLore);
	public static ItemBuilder TBD = new ItemBuilder(Material.STICK).name("To Be Determined").lore(BFQuests.itemLore).amount(1);

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
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(MainIsland.relic_base)
							.ingredient(goldIngot.clone().amount(2)));
				}};
			}
		},
		BAKER(2659) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
						.ingredient(new ItemBuilder(Material.BREAD).lore(BFQuests.itemLore).amount(64)));
				}};
			}
		},
		BARTENDER(2655) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
						.result(new ItemBuilder(Material.POTION).potionType(PotionType.POISON, true, false).lore(BFQuests.itemLore))
							.ingredient(goldNugget.clone().amount(3)));
					add(new TradeBuilder()
						.result(new ItemBuilder(Material.POTION).potionType(PotionType.WEAKNESS).lore(BFQuests.itemLore))
							.ingredient(goldNugget.clone().amount(3)));
					add(new TradeBuilder()
						.result(new ItemBuilder(Material.POTION).potionType(PotionType.SLOWNESS).lore(BFQuests.itemLore))
							.ingredient(goldNugget.clone().amount(3)));
				}};
			}
		},
		BLACKSMITH(2656) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(16))
						.ingredient(new ItemBuilder(Material.ANVIL).lore(BFQuests.itemLore)));
					add(new TradeBuilder()
						.result(new ItemBuilder(Material.COAL_BLOCK).lore(BFQuests.itemLore).amount(2).build())
						.ingredient(new ItemBuilder(Material.IRON_BLOCK).lore(BFQuests.itemLore).amount(1).build()));
				}};
			}
		},
		BOTANIST(2661) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
						.ingredient(new ItemBuilder(Material.MELON).amount(8).lore(BFQuests.itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
						.ingredient(new ItemBuilder(Material.PUMPKIN).amount(12).lore(BFQuests.itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
						.ingredient(new ItemBuilder(Material.HAY_BLOCK).amount(16).lore(BFQuests.itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
						.ingredient(new ItemBuilder(Material.SUGAR_CANE).amount(48).lore(BFQuests.itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
						.ingredient(new ItemBuilder(Material.BEETROOT).amount(32).lore(BFQuests.itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
						.ingredient(new ItemBuilder(Material.POTATO).amount(64).lore(BFQuests.itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
						.ingredient(new ItemBuilder(Material.CARROT).amount(64).lore(BFQuests.itemLore)));
				}};
			}
		},
		BREWER(2662) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(MainIsland.relic_eyes)
						.ingredient(new ItemBuilder(Material.HONEYCOMB).amount(9).lore(BFQuests.itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
						.ingredient(new ItemBuilder(Material.COCOA_BEANS).amount(12).lore(BFQuests.itemLore)));
				}};
			}
		},
		COLLECTOR(2750) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(18))
						.ingredient(new ItemBuilder(Material.GLISTERING_MELON_SLICE).lore(BFQuests.itemLore))
						.ingredient(new ItemBuilder(Material.GOLDEN_CARROT).lore(BFQuests.itemLore)));
					add(new TradeBuilder()
							.result(MainIsland.rareFlower)
							.ingredient(MainIsland.ancientPickaxe)
							.ingredient(MainIsland.relic));
					add(new TradeBuilder()
							.result(MinigameNightIsland.joystick)
							.ingredient(goldIngot.clone().amount(10)));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
						.ingredient(Loot.tigerTrout));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
						.ingredient(Loot.glacierfish));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
						.ingredient(Loot.crimsonfish));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
						.ingredient(Loot.flathead));
				}};
			}
		},
		FISHERMAN(2653) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
						.result(new ItemBuilder(Material.FISHING_ROD).enchant(Enchantment.LURE, 2).lore(BFQuests.itemLore))
							.ingredient(goldIngot.clone().amount(2)));
					// Default
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
						.ingredient(Loot.cod));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
						.ingredient(Loot.salmon));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
						.ingredient(Loot.tropicalFish));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
						.ingredient(Loot.pufferfish));
					// Generic
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
						.ingredient(Loot.bullhead));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
						.ingredient(Loot.sturgeon));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
						.ingredient(Loot.woodskip));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
						.ingredient(Loot.voidSalmon));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
						.ingredient(Loot.redSnapper));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
						.ingredient(Loot.redMullet));
					// Island
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(3))
						.ingredient(Loot.seaCucumber));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
						.ingredient(Loot.midnightCarp));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
						.ingredient(Loot.sunfish));
				}};
			}
		},
		INVENTOR(2660) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(MainIsland.relic)
							.ingredient(MainIsland.relic_body)
							.ingredient(MainIsland.relic_eyes));
					add(new TradeBuilder()
							.result(MainIsland.relic_body)
							.ingredient(MainIsland.relic_arms)
							.ingredient(MainIsland.relic_base));
				}};
			}
		},
		MINER(2743) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
						.result(new ItemBuilder(Material.IRON_PICKAXE).lore(BFQuests.itemLore).amount(1))
							.ingredient(goldIngot.clone().amount(1)));
					add(new TradeBuilder()
						.result(new ItemBuilder(Material.IRON_INGOT).lore(BFQuests.itemLore).amount(1))
						.ingredient(new ItemBuilder(MainIsland.unpurifiedMarble).clone().lore(BFQuests.itemLore).amount(3)));
					add(new TradeBuilder()
							.result(MainIsland.ancientPickaxe)
						.ingredient(new ItemBuilder(Material.COAL_BLOCK).lore(BFQuests.itemLore).amount(4).build()));
				}};
			}
		},
		PASTRY_CHEF(2654) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(MainIsland.honeyStroopWafel)
							.ingredient(MainIsland.stroofWafel)
							.ingredient(MainIsland.blessedHoneyBottle));
					add(new TradeBuilder()
							.result(MainIsland.stroofWafel)
						.ingredient(new ItemBuilder(Material.CAKE).lore(BFQuests.itemLore))
						.ingredient(new ItemBuilder(Material.COOKIE).amount(8).lore(BFQuests.itemLore)));
					add(new TradeBuilder()
						.result(new ItemBuilder(Material.EGG).lore(BFQuests.itemLore))
							.ingredient(goldNugget.clone().amount(5)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
						.ingredient(new ItemBuilder(Material.EGG).lore(BFQuests.itemLore)));
				}};
			}
		},
		SORCERER(2658) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(3))
						.ingredient(new ItemBuilder(Material.CAULDRON).lore(BFQuests.itemLore)));
					add(new TradeBuilder()
							.result(MainIsland.relic_arms)
							.ingredient(goldIngot.clone().amount(2)));
				}};
			}
//		},
//		// Temp disabled until a decent value for Gold Blocks --> BFP is found
//		TRADER(2763) {
//			@Override
//			public List<TradeBuilder> getTrades() {
//				return new ArrayList<TradeBuilder>() {{
//					add(new TradeBuilder()
//							.result(TBD)
//							.ingredient(goldBlock.clone().amount(8)));
//				}};
//			}
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
