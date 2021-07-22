package gg.projecteden.nexus.features.events.y2020.bearfair20.quests.npcs;

import gg.projecteden.nexus.features.events.y2020.bearfair20.islands.MainIsland;
import gg.projecteden.nexus.features.events.y2020.bearfair20.islands.MinigameNightIsland;
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

import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests.itemLore;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.bullhead;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.cod;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.crimsonfish;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.flathead;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.glacierfish;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.midnightCarp;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.pufferfish;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.redMullet;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.redSnapper;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.salmon;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.seaCucumber;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.sturgeon;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.sunfish;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.tigerTrout;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.tropicalFish;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.voidSalmon;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.woodskip;

// TODO: Trades & Economy
public class Merchants {

	public static ItemBuilder goldNugget = new ItemBuilder(Material.GOLD_NUGGET).lore(itemLore);
	public static ItemBuilder goldIngot = new ItemBuilder(Material.GOLD_INGOT).lore(itemLore);
	public static ItemBuilder goldBlock = new ItemBuilder(Material.GOLD_BLOCK).lore(itemLore);
	public static ItemBuilder TBD = new ItemBuilder(Material.STICK).name("To Be Determined").lore(itemLore).amount(1);


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
							.ingredient(new ItemBuilder(Material.BREAD).lore(itemLore).amount(64)));
				}};
			}
		},
		BARTENDER(2655) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potionType(PotionType.POISON, true, false).lore(itemLore))
							.ingredient(goldNugget.clone().amount(3)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potionType(PotionType.WEAKNESS).lore(itemLore))
							.ingredient(goldNugget.clone().amount(3)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.POTION).potionType(PotionType.SLOWNESS).lore(itemLore))
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
							.ingredient(new ItemBuilder(Material.ANVIL).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.COAL_BLOCK).lore(itemLore).amount(2).build())
							.ingredient(new ItemBuilder(Material.IRON_BLOCK).lore(itemLore).amount(1).build()));
				}};
			}
		},
		BOTANIST(2661) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.MELON).amount(8).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.PUMPKIN).amount(12).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.HAY_BLOCK).amount(16).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.SUGAR_CANE).amount(48).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.BEETROOT).amount(32).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.POTATO).amount(64).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(new ItemBuilder(Material.CARROT).amount(64).lore(itemLore)));
				}};
			}
		},
		BREWER(2662) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(MainIsland.relic_eyes)
							.ingredient(new ItemBuilder(Material.HONEYCOMB).amount(9).lore(itemLore)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
							.ingredient(new ItemBuilder(Material.COCOA_BEANS).amount(12).lore(itemLore)));
				}};
			}
		},
		COLLECTOR(2750) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(18))
							.ingredient(new ItemBuilder(Material.GLISTERING_MELON_SLICE).lore(itemLore))
							.ingredient(new ItemBuilder(Material.GOLDEN_CARROT).lore(itemLore)));
					add(new TradeBuilder()
							.result(MainIsland.rareFlower)
							.ingredient(MainIsland.ancientPickaxe)
							.ingredient(MainIsland.relic));
					add(new TradeBuilder()
							.result(MinigameNightIsland.joystick)
							.ingredient(goldIngot.clone().amount(10)));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
							.ingredient(tigerTrout));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
							.ingredient(glacierfish));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
							.ingredient(crimsonfish));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
							.ingredient(flathead));
				}};
			}
		},
		FISHERMAN(2653) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.FISHING_ROD).enchant(Enchantment.LURE, 2).lore(itemLore))
							.ingredient(goldIngot.clone().amount(2)));
					// Default
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(cod));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(salmon));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(1))
							.ingredient(tropicalFish));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
							.ingredient(pufferfish));
					// Generic
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
							.ingredient(bullhead));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
							.ingredient(sturgeon));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
							.ingredient(woodskip));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
							.ingredient(voidSalmon));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
							.ingredient(redSnapper));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
							.ingredient(redMullet));
					// Island
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(3))
							.ingredient(seaCucumber));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
							.ingredient(midnightCarp));
					add(new TradeBuilder()
							.result(goldBlock.clone().amount(1))
							.ingredient(sunfish));
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
							.result(new ItemBuilder(Material.IRON_PICKAXE).lore(itemLore).amount(1))
							.ingredient(goldIngot.clone().amount(1)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.IRON_INGOT).lore(itemLore).amount(1))
							.ingredient(new ItemBuilder(MainIsland.unpurifiedMarble).clone().lore(itemLore).amount(3)));
					add(new TradeBuilder()
							.result(MainIsland.ancientPickaxe)
							.ingredient(new ItemBuilder(Material.COAL_BLOCK).lore(itemLore).amount(4).build()));
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
							.ingredient(new ItemBuilder(Material.CAKE).lore(itemLore))
							.ingredient(new ItemBuilder(Material.COOKIE).amount(8).lore(itemLore)));
					add(new TradeBuilder()
							.result(new ItemBuilder(Material.EGG).lore(itemLore))
							.ingredient(goldNugget.clone().amount(5)));
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(2))
							.ingredient(new ItemBuilder(Material.EGG).lore(itemLore)));
				}};
			}
		},
		SORCERER(2658) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(goldNugget.clone().amount(3))
							.ingredient(new ItemBuilder(Material.CAULDRON).lore(itemLore)));
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
