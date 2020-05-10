package me.pugabyte.bncore.features.votes.vps;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSPage.VPSSlot;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSPage.VPSSlot.VPSSlotBuilder;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public enum VPSMenu {
	SURVIVAL {
		@Getter
		final List<VPSPage> pages = new ArrayList<VPSPage>() {{
			add(VPSPage.builder().items(new HashMap<Integer, VPSSlotBuilder>() {{
				put(10, VPSSlot.builder()
						.name("$250")
						.display(Material.IRON_NUGGET)
						.price(1)
						.money(250));
				put(11, VPSSlot.builder()
						.name("$2,500")
						.display(Material.GOLD_NUGGET)
						.price(10)
						.money(2500));
				put(12, VPSSlot.builder()
						.name("$5,000")
						.display(Material.GOLD_INGOT)
						.price(20)
						.money(5000));
				put(13, VPSSlot.builder()
						.name("$10,000")
						.display(Material.GOLD_BLOCK)
						.price(40)
						.money(10000));

				put(16, VPSSlot.builder()
						.name("x3 KillerMoney boost for 2 days")
						.display(Material.DIAMOND_SWORD, 3)
						.price(30)
						.consoleCommand("kmboost [player]")
						.takePoints(false)
						.close(true));
				put(34, VPSSlot.builder()
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

				put(28, VPSSlot.builder()
						.name("Full Diamond Armor Set")
						.display(Material.DIAMOND_CHESTPLATE)
						.price(28)
						.give(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS));
				put(29, VPSSlot.builder()
						.name("Full Iron Armor Set")
						.display(Material.IRON_CHESTPLATE)
						.price(18)
						.give(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS));
				put(30, VPSSlot.builder()
						.name("Full Golden Armor Set")
						.display(Material.GOLDEN_CHESTPLATE)
						.price(15)
						.give(Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS));
				put(31, VPSSlot.builder()
						.name("Full Chainmail Armor Set")
						.display(Material.CHAINMAIL_CHESTPLATE)
						.price(20)
						.give(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS));

				put(37, VPSSlot.builder()
						.name("Diamond Horse Armor")
						.displayAndGive(Material.DIAMOND_HORSE_ARMOR)
						.price(10));
				put(38, VPSSlot.builder()
						.name("Iron Horse Armor")
						.displayAndGive(Material.IRON_HORSE_ARMOR)
						.price(6));
				put(39, VPSSlot.builder()
						.name("Gold Horse Armor")
						.displayAndGive(Material.GOLDEN_HORSE_ARMOR)
						.price(8));
				put(40, VPSSlot.builder()
						.name("Leather Horse Armor")
						.displayAndGive(Material.LEATHER_HORSE_ARMOR)
						.price(4));
				put(41, VPSSlot.builder()
						.name("Saddle")
						.displayAndGive(Material.SADDLE)
						.price(10));
			}}).build());

			add(VPSPage.builder().items(new HashMap<Integer, VPSSlotBuilder>() {{
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
						.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.SILK_TOUCH, 1))
						.price(30));
				put(13, VPSSlot.builder()
						.name("Enchanted Book")
						.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.DURABILITY, 3))
						.price(30));
				put(14, VPSSlot.builder()
						.name("Enchanted Book")
						.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.MENDING, 1))
						.price(45));
				put(15, VPSSlot.builder()
						.name("Enchanted Book")
						.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.ARROW_INFINITE, 1))
						.price(40));
				put(16, VPSSlot.builder()
						.name("Enchanted Book")
						.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.THORNS, 1))
						.price(15));

				put(19, VPSSlot.builder()
						.name("Enchanted Book")
						.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.CHANNELING, 1))
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
						.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.MULTISHOT, 1))
						.price(35));
				put(24, VPSSlot.builder()
						.name("Enchanted Book")
						.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.QUICK_CHARGE, 3))
						.price(30));
				put(25, VPSSlot.builder()
						.name("Enchanted Book")
						.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.PIERCING, 4))
						.price(30));

				put(37, VPSSlot.builder()
						.name("10 Emeralds")
						.displayAndGive(Material.EMERALD, 10)
						.price(12));
				put(38, VPSSlot.builder()
						.name("10 Diamonds")
						.displayAndGive(Material.DIAMOND, 10)
						.price(15));
				put(39, VPSSlot.builder()
						.name("10 Gold Ingots")
						.displayAndGive(Material.GOLD_INGOT, 10)
						.price(7));
				put(40, VPSSlot.builder()
						.name("10 Iron Ingots")
						.displayAndGive(Material.IRON_INGOT, 10)
						.price(5));
				put(41, VPSSlot.builder()
						.name("10 Lapis Lazuli")
						.displayAndGive(Material.LAPIS_LAZULI, 10)
						.price(3));
				put(42, VPSSlot.builder()
						.name("16 Quartz")
						.displayAndGive(Material.QUARTZ, 16)
						.price(2));
				put(43, VPSSlot.builder()
						.name("32 Coal")
						.displayAndGive(Material.COAL, 32)
						.price(2));
			}}).build());

			add(VPSPage.builder().items(new HashMap<Integer, VPSSlotBuilder>() {{
				put(10, VPSSlot.builder()
						.name("2 Horse Spawn Eggs")
						.displayAndGive(Material.HORSE_SPAWN_EGG, 2)
						.price(100));
				put(11, VPSSlot.builder()
						.name("2 Donkey Spawn Eggs")
						.displayAndGive(Material.DONKEY_SPAWN_EGG, 2)
						.price(100));
				put(12, VPSSlot.builder()
						.name("2 Llama Spawn Eggs")
						.displayAndGive(Material.LLAMA_SPAWN_EGG, 2)
						.price(100));
				put(13, VPSSlot.builder()
						.name("2 Cow Spawn Eggs")
						.displayAndGive(Material.COW_SPAWN_EGG, 2)
						.price(150));
				put(14, VPSSlot.builder()
						.name("2 Mooshroom Spawn Eggs")
						.displayAndGive(Material.MOOSHROOM_SPAWN_EGG, 2)
						.price(200));
				put(15, VPSSlot.builder()
						.name("2 Sheep Spawn Eggs")
						.displayAndGive(Material.SHEEP_SPAWN_EGG, 2)
						.price(100));
				put(16, VPSSlot.builder()
						.name("2 Pig Spawn Eggs")
						.displayAndGive(Material.PIG_SPAWN_EGG, 2)
						.price(50));

				put(19, VPSSlot.builder()
						.name("2 Villager Spawn Eggs")
						.displayAndGive(Material.VILLAGER_SPAWN_EGG, 2)
						.price(200));
				put(20, VPSSlot.builder()
						.name("2 Fox Spawn Eggs")
						.displayAndGive(Material.FOX_SPAWN_EGG, 2)
						.price(200));
				put(21, VPSSlot.builder()
						.name("2 Ocelot Spawn Eggs")
						.displayAndGive(Material.OCELOT_SPAWN_EGG, 2)
						.price(50));
				put(22, VPSSlot.builder()
						.name("2 Cat Spawn Eggs")
						.displayAndGive(Material.CAT_SPAWN_EGG, 2)
						.price(35));
				put(23, VPSSlot.builder()
						.name("2 Wolf Spawn Eggs")
						.displayAndGive(Material.WOLF_SPAWN_EGG, 2)
						.price(35));
				put(24, VPSSlot.builder()
						.name("2 Bunny Spawn Eggs")
						.displayAndGive(Material.RABBIT_SPAWN_EGG, 2)
						.price(50));
				put(25, VPSSlot.builder()
						.name("2 Parrot Spawn Eggs")
						.displayAndGive(Material.PARROT_SPAWN_EGG, 2)
						.price(75));

				put(30, VPSSlot.builder()
						.name("2 Panda Spawn Eggs")
						.displayAndGive(Material.PANDA_SPAWN_EGG, 2)
						.price(100));
				put(31, VPSSlot.builder()
						.name("2 Dolphin Spawn Eggs")
						.displayAndGive(Material.DOLPHIN_SPAWN_EGG, 2)
						.price(150));
				put(32, VPSSlot.builder()
						.name("2 Turtle Spawn Eggs")
						.displayAndGive(Material.TURTLE_SPAWN_EGG, 2)
						.price(75));

				put(40, VPSSlot.builder()
						.name("1 Name Tag")
						.displayAndGive(Material.NAME_TAG)
						.price(2));
			}}).build());

			add(VPSPage.builder().items(new HashMap<Integer, VPSSlotBuilder>() {{
				put(10, VPSSlot.builder()
						.name("32 Packed Ice")
						.displayAndGive(Material.PACKED_ICE, 32)
						.price(15));
				put(11, VPSSlot.builder()
						.name("32 Sea Lanterns")
						.displayAndGive(Material.SEA_LANTERN, 32)
						.price(10));
				put(12, VPSSlot.builder()
						.name("32 Glowstone")
						.displayAndGive(Material.GLOWSTONE, 32)
						.price(7));
				put(13, VPSSlot.builder()
						.name("32 Magma Blocks")
						.displayAndGive(Material.MAGMA_BLOCK, 32)
						.price(10));
				put(14, VPSSlot.builder()
						.name("1 Sponge")
						.displayAndGive(Material.SPONGE)
						.price(35));
				put(15, VPSSlot.builder()
						.name("8 Honeycomb Blocks")
						.displayAndGive(Material.HONEYCOMB_BLOCK)
						.price(15));
				put(16, VPSSlot.builder()
						.name("8 Honey Blocks")
						.displayAndGive(Material.HONEY_BLOCK)
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
						.name("5 Scutes")
						.displayAndGive(Material.SCUTE)
						.price(30));

				put(37, VPSSlot.builder()
						.name("64 Experience Bottles")
						.displayAndGive(Material.EXPERIENCE_BOTTLE, 64)
						.price(15));
				put(38, VPSSlot.builder()
						.name("1 Notch Apple")
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

			add(VPSPage.builder().items(new HashMap<Integer, VPSSlotBuilder>() {{
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
				put(23, VPSSlot.builder()
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

				put(38, VPSSlot.builder()
						.name("1 dragon head")
						.displayAndGive(Material.DRAGON_HEAD)
						.price(50));
				put(42, VPSSlot.builder()
						.name("4 ender crystals")
						.displayAndGive(Material.END_CRYSTAL, 4)
						.price(20));
				put(39, VPSSlot.builder()
						.name("1 shulker box")
						.displayAndGive(Material.SHULKER_BOX)
						.price(80));
				put(43, VPSSlot.builder()
						.name("1 elytra")
						.displayAndGive(Material.ELYTRA)
						.price(40));
				put(41, VPSSlot.builder()
						.name("1 totem of undying")
						.displayAndGive(Material.TOTEM_OF_UNDYING)
						.price(50));
			}}).build());
		}};
	},

	CREATIVE {
		@Getter
		final List<VPSPage> pages = new ArrayList<VPSPage>() {{
			add(VPSPage.builder()
					.rows(3)
					.items(new HashMap<Integer, VPSSlotBuilder>() {{
						put(11, VPSSlot.builder()
								.name("+1 Plot")
								.display(new ItemBuilder(Material.SANDSTONE_SLAB)
										.lore("&3You may purchase up to")
										.lore("&efour &3addition plots either")
										.lore("&3from the VPS or the server")
										.lore("&3store (&c/donate&3)")
										.lore("")
										.lore("&6Price: &e150vp"))
								.command("vps buy plot"));
						put(13, VPSSlot.builder()
								.name("Player Heads")
								.display(new ItemBuilder(Material.PLAYER_HEAD)
										.lore("&6Price &3(Non-Staff): &e6vp")
										.lore("&6Price &3(Staff): &e9vp"))
								.command("vps buy head")
								.close(true));
						put(15, VPSSlot.builder()
								.name("Uncraftable Banners")
								.display(new ItemBuilder(Material.CYAN_BANNER)
										.lore("&eClick to teleport &3to the")
										.lore("&3banner display area")
										.lore("")
										.lore("&3Read the &ehologram&3!")
										.lore("")
										.lore("&6Price: &e5-10vp"))
								.command("warp banners")
								.close(true));
					}}).build());
		}};
	},

	SKYBLOCK {
		@Getter
		final List<VPSPage> pages = new ArrayList<VPSPage>() {{
			add(VPSPage.builder()
					.rows(5)
					.items(new HashMap<Integer, VPSSlotBuilder>() {{

						put(10, VPSSlot.builder()
								.name("5 dirt")
								.displayAndGive(Material.DIRT, 5)
								.price(10));
						put(11, VPSSlot.builder()
								.name("1 grass")
								.displayAndGive(Material.GRASS_BLOCK, 1)
								.price(5));
						put(12, VPSSlot.builder()
								.name("64 cobble")
								.displayAndGive(Material.COBBLESTONE, 64)
								.price(5));
						put(14, VPSSlot.builder()
								.name("1 water bucket")
								.displayAndGive(Material.WATER_BUCKET)
								.price(25));
						put(15, VPSSlot.builder()
								.name("1 lava bucket")
								.displayAndGive(Material.LAVA_BUCKET)
								.price(35));
						put(16, VPSSlot.builder()
								.name("1 iron pickaxe")
								.displayAndGive(Material.IRON_PICKAXE)
								.price(10));
						put(28, VPSSlot.builder()
								.name("5 seeds")
								.displayAndGive(Material.WHEAT_SEEDS, 5)
								.price(5));
						put(29, VPSSlot.builder()
								.name("1 pumpkin seeds")
								.displayAndGive(Material.PUMPKIN_SEEDS)
								.price(3));
						put(30, VPSSlot.builder()
								.name("1 melon seeds")
								.displayAndGive(Material.MELON_SEEDS)
								.price(3));
						put(31, VPSSlot.builder()
								.name("3 bone meal")
								.displayAndGive(new ItemStack(Material.BONE_MEAL, 3))
								.price(1));
						put(32, VPSSlot.builder()
								.name("8 bread")
								.displayAndGive(Material.BREAD, 8)
								.price(3));
						put(33, VPSSlot.builder()
								.name("16 torches")
								.displayAndGive(Material.TORCH, 16)
								.price(5));
						put(34, VPSSlot.builder()
								.name("Reset your deaths to 0")
								.display(Material.TOTEM_OF_UNDYING)
								.price(40)
								.consoleCommand("asadmin setdeaths [player] 0"));
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
		@Default
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

				public VPSSlot build() {
					ItemBuilder.setName(this.display, "&3&l" + this.name);
					ItemBuilder.addItemFlags(this.display, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
					return new VPSSlot(this.display, this.price, this.takePoints, this.close, this.money, this.items, this.command, this.consoleCommand);
				}
			}
		}
	}
}
