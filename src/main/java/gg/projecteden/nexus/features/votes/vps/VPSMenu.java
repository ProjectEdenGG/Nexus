package gg.projecteden.nexus.features.votes.vps;

import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.votes.vps.VPSMenu.VPSPage.VPSSlot;
import gg.projecteden.nexus.features.votes.vps.VPSMenu.VPSPage.VPSSlot.VPSSlotBuilder;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public enum VPSMenu {
	SURVIVAL {
		@Getter
		final List<VPSPage> pages = new ArrayList<>() {{
			add(VPSPage.builder().items(new HashMap<>() {{
				put(10, VPSSlot.builder()
					.name("$250")
					.display(CustomMaterial.GOLD_COINS_2)
					.price(1)
					.money(250));
				put(11, VPSSlot.builder()
					.name("$2,500")
					.display(CustomMaterial.GOLD_COINS_4)
					.price(10)
					.money(2500));
				put(12, VPSSlot.builder()
					.name("$5,000")
					.display(CustomMaterial.GOLD_COINS_7)
					.price(20)
					.money(5000));
				put(13, VPSSlot.builder()
					.name("$10,000")
					.display(CustomMaterial.GOLD_COINS_9)
					.price(40)
					.money(10000));

				put(28, VPSSlot.builder()
					.name("1 Vote Crate Key")
					.display(new ItemBuilder(CustomMaterial.CRATE_KEY_VOTE).amount(1))
					.price(2)
					.onPurchase((player, item) -> CrateType.VOTE.giveVPS(player, 1)));
				put(29, VPSSlot.builder()
					.name("8 Vote Crate Key")
					.display(new ItemBuilder(CustomMaterial.CRATE_KEY_VOTE).amount(8))
					.price(16)
					.onPurchase((player, item) -> CrateType.VOTE.giveVPS(player, 8)));
				put(30, VPSSlot.builder()
					.name("16 Vote Crate Key")
					.display(new ItemBuilder(CustomMaterial.CRATE_KEY_VOTE).amount(16))
					.price(32)
					.onPurchase((player, item) -> CrateType.VOTE.giveVPS(player, 16)));
				put(31, VPSSlot.builder()
					.name("32 Vote Crate Key")
					.display(new ItemBuilder(CustomMaterial.CRATE_KEY_VOTE).amount(32))
					.price(64)
					.onPurchase((player, item) -> CrateType.VOTE.giveVPS(player, 32)));

				put(16, VPSSlot.builder()
					.name("Uncraftable Banners")
					.display(new ItemBuilder(Material.CYAN_BANNER)
						.lore("&3Pre-selected banners or")
						.lore("&3choose your own!")
						.lore("")
						.lore("&eClick to teleport &3to the")
						.lore("&3banner display area")
						.lore("")
						.lore("&3Read the &ehologram&3!")
						.lore("")
						.lore("&6Price: &e5-10vp"))
					.command("warp banners")
					.close(true));

//				put(34, VPSSlot.builder()
//						.name("Easel")
//						.display(new ItemBuilder(Material.ARMOR_STAND))
//						.command("artmap give [player] easel")
//						.price(5));
//				put(43, VPSSlot.builder()
//						.name("Canvas")
//						.display(new ItemBuilder(Material.PAPER))
//						.consoleCommand("artmap give [player] canvas")
//						.price(10));
			}}).build());

			add(VPSPage.builder().items(new HashMap<>() {{
				put(11, VPSSlot.builder()
					.name("Full Diamond Armor Set")
					.display(Material.DIAMOND_CHESTPLATE)
					.price(28)
					.give(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS));
				put(12, VPSSlot.builder()
					.name("Full Iron Armor Set")
					.display(Material.IRON_CHESTPLATE)
					.price(18)
					.give(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS));
				put(14, VPSSlot.builder()
					.name("Full Golden Armor Set")
					.display(Material.GOLDEN_CHESTPLATE)
					.price(15)
					.give(Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS));
				put(15, VPSSlot.builder()
					.name("Full Chainmail Armor Set")
					.display(Material.CHAINMAIL_CHESTPLATE)
					.price(20)
					.give(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS));

				put(20, VPSSlot.builder()
					.name("Diamond Horse Armor")
					.displayAndGive(Material.DIAMOND_HORSE_ARMOR)
					.price(10));
				put(21, VPSSlot.builder()
					.name("Iron Horse Armor")
					.displayAndGive(Material.IRON_HORSE_ARMOR)
					.price(6));
				put(23, VPSSlot.builder()
					.name("Gold Horse Armor")
					.displayAndGive(Material.GOLDEN_HORSE_ARMOR)
					.price(8));
				put(24, VPSSlot.builder()
					.name("Leather Horse Armor")
					.displayAndGive(Material.LEATHER_HORSE_ARMOR)
					.price(4));
				put(22, VPSSlot.builder()
					.name("Saddle")
					.displayAndGive(Material.SADDLE)
					.price(10));

				put(37, VPSSlot.builder()
					.name("1 Ancient Debris")
					.displayAndGive(Material.ANCIENT_DEBRIS)
					.price(3));
				put(38, VPSSlot.builder()
					.name("10 Emeralds")
					.displayAndGive(Material.EMERALD, 10)
					.price(12));
				put(39, VPSSlot.builder()
					.name("10 Diamonds")
					.displayAndGive(Material.DIAMOND, 10)
					.price(15));
				put(40, VPSSlot.builder()
					.name("10 Gold Ingots")
					.displayAndGive(Material.GOLD_INGOT, 10)
					.price(7));
				put(41, VPSSlot.builder()
					.name("10 Iron Ingots")
					.displayAndGive(Material.IRON_INGOT, 10)
					.price(5));
				put(42, VPSSlot.builder()
					.name("10 Lapis Lazuli")
					.displayAndGive(Material.LAPIS_LAZULI, 10)
					.price(3));
				put(43, VPSSlot.builder()
					.name("16 Quartz")
					.displayAndGive(Material.QUARTZ, 16)
					.price(2));
			}}).build());

			add(VPSPage.builder().items(new HashMap<>() {{
				put(10, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.FROST_WALKER, 2))
					.price(30));
				put(11, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.DEPTH_STRIDER, 3))
					.price(35));
				put(12, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.SOUL_SPEED, 3))
					.price(45));
				put(13, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.SILK_TOUCH))
					.price(30));
				put(14, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.UNBREAKING, 3))
					.price(30));
				put(15, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.INFINITY))
					.price(40));
				put(16, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.THORNS))
					.price(15));

				put(19, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.CHANNELING))
					.price(30));
				put(20, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.LOYALTY, 3))
					.price(40));
				put(21, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.IMPALING, 5))
					.price(38));
				put(22, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.RIPTIDE, 3))
					.price(50));
				put(23, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.MULTISHOT))
					.price(35));
				put(24, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.QUICK_CHARGE, 3))
					.price(30));
				put(25, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.PIERCING, 4))
					.price(30));

				put(31, VPSSlot.builder()
					.name("Enchanted Book")
					.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.MENDING))
					.price(45));
			}}).build());

			add(VPSPage.builder().items(new HashMap<>() {{
				put(10, VPSSlot.builder()
					.name("8 Blue Ice")
					.displayAndGive(Material.BLUE_ICE, 8)
					.price(36));
				put(11, VPSSlot.builder()
					.name("32 Sea Lanterns")
					.displayAndGive(Material.SEA_LANTERN, 32)
					.price(10));
				put(12, VPSSlot.builder()
					.name("32 Glowstone")
					.displayAndGive(Material.GLOWSTONE, 32)
					.price(7));
				put(13, VPSSlot.builder()
					.name("32 Shroomlight")
					.displayAndGive(Material.SHROOMLIGHT, 32)
					.price(10));
				put(14, VPSSlot.builder()
					.name("1 Sponge")
					.displayAndGive(Material.SPONGE)
					.price(35));
				put(15, VPSSlot.builder()
					.name("8 Honeycomb Blocks")
					.displayAndGive(Material.HONEYCOMB_BLOCK, 8)
					.price(15));
				put(16, VPSSlot.builder()
					.name("8 Honey Blocks")
					.displayAndGive(Material.HONEY_BLOCK, 8)
					.price(20));

				put(28, VPSSlot.builder()
					.name("4 Blaze Rods")
					.displayAndGive(Material.BLAZE_ROD, 4)
					.price(8));
				put(29, VPSSlot.builder()
					.name("8 Ender Pearls")
					.displayAndGive(Material.ENDER_PEARL, 8)
					.price(2));
				put(30, VPSSlot.builder()
					.name("4 Slimeballs")
					.displayAndGive(Material.SLIME_BALL, 4)
					.price(5));
				put(31, VPSSlot.builder()
					.name("16 Gunpowder")
					.displayAndGive(Material.GUNPOWDER, 16)
					.price(2));
				put(32, VPSSlot.builder()
					.name("16 Leather")
					.displayAndGive(Material.LEATHER, 16)
					.price(10));
				put(33, VPSSlot.builder()
					.name("16 Phantom Membranes")
					.displayAndGive(Material.PHANTOM_MEMBRANE, 16)
					.price(12));
				put(34, VPSSlot.builder()
					.name("1 Name Tag")
					.displayAndGive(Material.NAME_TAG)
					.price(2));

				put(37, VPSSlot.builder()
					.name("64 Experience Bottles")
					.displayAndGive(Material.EXPERIENCE_BOTTLE, 64)
					.price(15));
				put(38, VPSSlot.builder()
					.name("1 Enchanted Golden Apple")
					.displayAndGive(Material.ENCHANTED_GOLDEN_APPLE)
					.price(30));
				put(39, VPSSlot.builder()
					.name("1 Golden Apple")
					.displayAndGive(Material.GOLDEN_APPLE)
					.price(10));
				put(40, VPSSlot.builder()
					.name("16 Bones")
					.displayAndGive(Material.BONE, 16)
					.price(2));
				put(41, VPSSlot.builder()
					.name("4 Steak")
					.displayAndGive(Material.COOKED_BEEF, 4)
					.price(1));
				put(42, VPSSlot.builder()
					.name("16 Sweet Berries")
					.displayAndGive(Material.SWEET_BERRIES, 16)
					.price(1));
				put(43, VPSSlot.builder()
					.name("16 Sea Pickles")
					.displayAndGive(Material.SEA_PICKLE, 16)
					.price(5));
			}}).build());

			add(VPSPage.builder().items(new HashMap<>() {{
				put(10, VPSSlot.builder()
					.name("4 Oak Saplings")
					.displayAndGive(Material.OAK_SAPLING, 4)
					.price(2));
				put(11, VPSSlot.builder()
					.name("4 Spruce Saplings")
					.displayAndGive(Material.SPRUCE_SAPLING, 4)
					.price(8));
				put(12, VPSSlot.builder()
					.name("4 Birch Saplings")
					.displayAndGive(Material.BIRCH_SAPLING, 4)
					.price(4));
				put(13, VPSSlot.builder()
					.name("4 Jungle Saplings")
					.displayAndGive(Material.JUNGLE_SAPLING, 4)
					.price(6));
				put(14, VPSSlot.builder()
					.name("4 Acacia Saplings")
					.displayAndGive(Material.ACACIA_SAPLING, 4)
					.price(4));
				put(15, VPSSlot.builder()
					.name("4 Dark Oak Saplings")
					.displayAndGive(Material.DARK_OAK_SAPLING, 4)
					.price(6));
				put(16, VPSSlot.builder()
					.name("16 Bamboo")
					.displayAndGive(Material.BAMBOO, 16)
					.price(5));

				put(29, VPSSlot.builder()
					.name("1 Wither Skeleton Skull")
					.displayAndGive(Material.WITHER_SKELETON_SKULL)
					.price(40));
				put(30, VPSSlot.builder()
					.name("4 Wither Rose")
					.displayAndGive(Material.WITHER_ROSE, 4)
					.price(120));
				put(31, VPSSlot.builder()
					.name("1 Beacon")
					.displayAndGive(Material.BEACON)
					.price(250));
				put(32, VPSSlot.builder()
					.name("1 Trident")
					.displayAndGive(Material.TRIDENT)
					.price(80));
				put(33, VPSSlot.builder()
					.name("1 Heart of the Sea")
					.displayAndGive(Material.HEART_OF_THE_SEA)
					.price(60));

				put(37, VPSSlot.builder()
					.name("4 Crying Obsidian")
					.displayAndGive(Material.CRYING_OBSIDIAN, 4)
					.price(10));
				put(38, VPSSlot.builder()
					.name("1 Dragon Head")
					.displayAndGive(Material.DRAGON_HEAD)
					.price(50));
				put(39, VPSSlot.builder()
					.name("4 Ender Crystals")
					.displayAndGive(Material.END_CRYSTAL, 4)
					.price(20));
				put(40, VPSSlot.builder()
					.name("1 Shulker Box")
					.displayAndGive(Material.SHULKER_BOX)
					.price(50));
				put(41, VPSSlot.builder()
					.name("1 Elytra")
					.displayAndGive(Material.ELYTRA)
					.price(40));
				put(42, VPSSlot.builder()
					.name("1 Totem of Undying")
					.displayAndGive(Material.TOTEM_OF_UNDYING)
					.price(50));
				put(43, VPSSlot.builder()
					.name("1 Pigstep Music Disc")
					.displayAndGive(Material.MUSIC_DISC_PIGSTEP)
					.price(30));
			}}).build());
		}};
	},

	CREATIVE {
		@Getter
		final List<VPSPage> pages = new ArrayList<>() {{
			add(VPSPage.builder()
				.rows(3)
				.items(new HashMap<>() {{
					put(13, VPSSlot.builder()
						.name("+1 Plot")
						.display(new ItemBuilder(Material.SANDSTONE_SLAB)
							.lore("&3You may purchase up to")
							.lore("&efour &3additional plots either")
							.lore("&3from the VPS or the server &c/store")
							.lore("")
							.lore("&6Price: &e150vp"))
						.command("vote points store buy plot"));
				}}).build());
		}};
	},

	SKYBLOCK {
		@Getter
		final List<VPSPage> pages = new ArrayList<>() {{
			add(VPSPage.builder()
				.rows(3)
				.items(new HashMap<>() {{
					put(12, VPSSlot.builder()
						.name("+25 Island Range")
						.display(new ItemBuilder(Material.MAGENTA_GLAZED_TERRACOTTA)
							.lore("&3By default, your island is &e100x100")
							.lore("&3Purchase additional space up to &e400x400")
							.lore("")
							.lore("&6Price: &e50vp"))
						.command("vote points store buy oneblock-expansion"));
					put(15, VPSSlot.builder()
						.name("Random Armor Trim")
						.display(Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE)
						.price(10)
						.onPurchase((player, slot) -> {
							PlayerUtils.giveItem(player, RandomUtils.randomMaterial(MaterialTag.ARMOR_TRIM));
							return true;
						}));
				}}).build());
		}};
	};

	public abstract List<VPSPage> getPages();

	public VPSPage getPage(int page) {
		return getPages().get(page - 1);
	}

	public int indexOf(VPSPage page) {
		return getPages().indexOf(page);
	}

	public boolean isFirst(VPSPage page) {
		return indexOf(page) == 0;
	}

	public boolean isLast(VPSPage page) {
		return indexOf(page) == getPages().size() - 1;
	}

	@Data
	public static class VPSPage {
		private int rows = 6;
		private Map<Integer, VPSSlot> items;

		@Builder
		public VPSPage(int rows, HashMap<Integer, VPSSlotBuilder> items) {
			this.rows = rows == 0 ? 6 : rows;
			this.items = items.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().build()));
		}

		@Data
		@Builder
		public static class VPSSlot {
			private ItemStack display;

			private int price;
			private boolean takePoints;
			private boolean close;

			private int money;
			private List<ItemStack> items;
			private String command;
			private String consoleCommand;
			private BiPredicate<Player, VPSSlot> onPurchase;

			public String getName() {
				return display.getItemMeta().getDisplayName();
			}

			public static class VPSSlotBuilder {
				private String name;
				private ItemStack display;

				private int price;
				private boolean takePoints;
				private boolean close;

				private int money;
				private List<ItemStack> items;
				private String command;
				private String consoleCommand;

				VPSSlotBuilder() {
				}

				public VPSSlotBuilder name(String name) {
					this.name = name;
					return this;
				}

				public VPSSlotBuilder display(Material display) {
					return display(new ItemStack(display));
				}

				public VPSSlotBuilder display(CustomMaterial display) {
					return display(new ItemBuilder(display));
				}

				public VPSSlotBuilder display(Material display, int amount) {
					return display(new ItemStack(display, amount));
				}

				public VPSSlotBuilder display(ItemBuilder display) {
					return display(display.build());
				}

				public VPSSlotBuilder display(ItemStack display) {
					this.display = display;
					return this;
				}

				public VPSSlotBuilder displayAndGive(Material display) {
					display(new ItemStack(display));
					return give(new ItemStack(display));
				}

				public VPSSlotBuilder displayAndGive(Material display, int amount) {
					display(new ItemStack(display, amount));
					return give(new ItemStack(display, amount));
				}

				public VPSSlotBuilder displayAndGive(ItemBuilder display) {
					display(display.build());
					return give(display.build());
				}

				public VPSSlotBuilder displayAndGive(ItemStack display) {
					display(display);
					return give(display);
				}

				public VPSSlotBuilder price(int price) {
					this.price = price;
					return this;
				}

				public VPSSlotBuilder takePoints(boolean takePoints) {
					this.takePoints = takePoints;
					return this;
				}

				public VPSSlotBuilder close(boolean close) {
					this.close = close;
					return this;
				}

				public VPSSlotBuilder money(int money) {
					this.money = money;
					return this;
				}

				public VPSSlotBuilder give(Material... materials) {
					return give(Arrays.stream(materials).map(ItemStack::new).collect(Collectors.toList()));
				}

				public VPSSlotBuilder give(Material material, int amount) {
					return give(new ItemStack(material, amount));
				}

				public VPSSlotBuilder give(ItemBuilder builder) {
					return give(builder.build());
				}

				public VPSSlotBuilder give(ItemStack... items) {
					return give(Arrays.stream(items).collect(Collectors.toList()));
				}

				public VPSSlotBuilder give(List<ItemStack> items) {
					this.items = items;
					return this;
				}

				public VPSSlotBuilder command(String command) {
					this.command = command;
					return this;
				}

				public VPSSlotBuilder consoleCommand(String consoleCommand) {
					this.consoleCommand = consoleCommand;
					return this;
				}

				public VPSSlotBuilder onPurchase(BiPredicate<Player, VPSSlot> onPurchase) {
					this.onPurchase = onPurchase;
					return this;
				}

				public VPSSlot build() {
					this.display = new ItemBuilder(this.display)
						.name("&3&l" + this.name)
						.itemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
						.build();
					return new VPSSlot(this.display, this.price, this.takePoints, this.close, this.money, this.items, this.command, this.consoleCommand, this.onPurchase);
				}
			}
		}
	}
}
