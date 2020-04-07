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
		List<VPSPage> pages = new ArrayList<VPSPage>() {{
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
						put(19, VPSSlot.builder()
								.name("Coal Protection Stone")
								.display(new ItemBuilder(Material.COAL_ORE)
										.lore("&3Size: &e11x11x11 &3(Radius of 5)")
										.lore("&3Do &c/ps about &3for more info"))
								.price(4)
								.give(Material.COAL_ORE));
						put(20, VPSSlot.builder()
								.name("Lapis Protection Stone")
								.display(new ItemBuilder(Material.LAPIS_ORE)
										.lore("&3Size: &e21x21x21 &3(Radius of 10)")
										.lore("&3Do &c/ps about &3for more info"))
								.price(15)
								.give(Material.LAPIS_ORE));
						put(21, VPSSlot.builder()
								.name("Diamond Protection Stone")
								.display(new ItemBuilder(Material.DIAMOND_ORE)
										.lore("&3Size: &e41x41x41 &3(Radius of 20)")
										.lore("&3Do &c/ps about &3for more info"))
								.price(50)
								.give(Material.DIAMOND_ORE));
						put(22, VPSSlot.builder()
								.name("Emerald Protection Stone")
								.display(new ItemBuilder(Material.EMERALD_ORE)
										.lore("&3Size: &e81x81x81 &3(Radius of 40)")
										.lore("&3Do &c/ps about &3for more info"))
								.price(100)
								.give(Material.EMERALD_ORE));
						put(38, VPSSlot.builder()
								.name("x3 KillerMoney boost for 2 days")
								.display(new ItemStack(Material.DIAMOND_SWORD, 3))
								.price(30)
								.consoleCommand("kmboost [player]")
								.takePoints(false)
								.close(true));
						put(40, VPSSlot.builder()
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
						put(15, VPSSlot.builder()
								.name("Diamond Horse Armor")
								.displayAndGive(Material.DIAMOND_HORSE_ARMOR)
								.price(10));
						put(24, VPSSlot.builder()
								.name("Iron Horse Armor")
								.displayAndGive(Material.IRON_HORSE_ARMOR)
								.price(6));
						put(33, VPSSlot.builder()
								.name("Gold Horse Armor")
								.displayAndGive(Material.GOLDEN_HORSE_ARMOR)
								.price(8));
						put(42, VPSSlot.builder()
								.name("Saddle")
								.displayAndGive(Material.SADDLE)
								.price(10));
						put(16, VPSSlot.builder()
								.name("Full Diamond Armor Set")
								.display(Material.DIAMOND_CHESTPLATE)
								.price(28)
								.give(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS));
						put(25, VPSSlot.builder()
								.name("Full Iron Armor Set")
								.display(Material.IRON_CHESTPLATE)
								.price(18)
								.give(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS));
						put(34, VPSSlot.builder()
								.name("Full Golden Armor Set")
								.display(Material.GOLDEN_CHESTPLATE)
								.price(15)
								.give(Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS));
						put(43, VPSSlot.builder()
								.name("Full Chainmail Armor Set")
								.display(Material.CHAINMAIL_CHESTPLATE)
								.price(20)
								.give(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS));
					}}).build());

			add(VPSPage.builder().items(new HashMap<Integer, VPSSlotBuilder>() {{
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
								.displayAndGive(new ItemStack(Material.LAPIS_LAZULI, 10))
								.price(3));
						put(42, VPSSlot.builder()
								.name("16 Quartz")
								.displayAndGive(Material.QUARTZ, 16)
								.price(2));
						put(43, VPSSlot.builder()
								.name("32 Coal")
								.displayAndGive(Material.COAL, 32)
								.price(2));
						put(11, VPSSlot.builder()
								.name("Enchanted Book")
								.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.FROST_WALKER, 2))
								.price(30));
						put(12, VPSSlot.builder()
								.name("Enchanted Book")
								.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.DURABILITY, 3))
								.price(30));
						put(13, VPSSlot.builder()
								.name("Enchanted Book")
								.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.MENDING, 1))
								.price(45));
						put(14, VPSSlot.builder()
								.name("Enchanted Book")
								.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.SWEEPING_EDGE, 3))
								.price(35));
						put(15, VPSSlot.builder()
								.name("Enchanted Book")
								.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.ARROW_INFINITE, 1))
								.price(40));
						put(22, VPSSlot.builder()
								.name("Enchanted Book")
								.displayAndGive(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.THORNS, 1))
								.price(15));
					}}).build());

			add(VPSPage.builder().items(new HashMap<Integer, VPSSlotBuilder>() {{
						put(10, VPSSlot.builder()
								.name("2 wolf spawn eggs")
								.displayAndGive(new ItemBuilder(Material.WOLF_SPAWN_EGG).amount(2))
								.price(25));
						put(11, VPSSlot.builder()
								.name("2 ocelot spawn eggs")
								.displayAndGive(new ItemBuilder(Material.OCELOT_SPAWN_EGG).amount(2))
								.price(35));
						put(12, VPSSlot.builder()
								.name("2 horse spawn eggs")
								.displayAndGive(new ItemBuilder(Material.HORSE_SPAWN_EGG).amount(2))
								.price(100));
						put(13, VPSSlot.builder()
								.name("2 donkey spawn eggs")
								.displayAndGive(new ItemBuilder(Material.DONKEY_SPAWN_EGG).amount(2))
								.price(100));
						put(14, VPSSlot.builder()
								.name("2 llama spawn eggs")
								.displayAndGive(new ItemBuilder(Material.LLAMA_SPAWN_EGG).amount(2))
								.price(100));
						put(15, VPSSlot.builder()
								.name("2 cow spawn eggs")
								.displayAndGive(new ItemBuilder(Material.COW_SPAWN_EGG).amount(2))
								.price(150));
						put(16, VPSSlot.builder()
								.name("2 sheep spawn eggs")
								.displayAndGive(new ItemBuilder(Material.SHEEP_SPAWN_EGG).amount(2))
								.price(100));
						put(20, VPSSlot.builder()
								.name("2 bunny spawn eggs")
								.displayAndGive(new ItemBuilder(Material.RABBIT_SPAWN_EGG).amount(2))
								.price(50));
						put(21, VPSSlot.builder()
								.name("2 villager spawn eggs")
								.displayAndGive(new ItemBuilder(Material.VILLAGER_SPAWN_EGG).amount(2))
								.price(200));
						put(22, VPSSlot.builder()
								.name("2 mooshroom spawn eggs")
								.displayAndGive(new ItemBuilder(Material.MOOSHROOM_SPAWN_EGG).amount(2))
								.price(200));
						put(23, VPSSlot.builder()
								.name("2 parrot spawn eggs")
								.displayAndGive(new ItemBuilder(Material.PARROT_SPAWN_EGG).amount(2))
								.price(75));
						put(24, VPSSlot.builder()
								.name("2 pig spawn eggs")
								.displayAndGive(new ItemBuilder(Material.PIG_SPAWN_EGG).amount(2))
								.price(50));
						put(31, VPSSlot.builder()
								.name("1 name tag")
								.displayAndGive(Material.NAME_TAG)
								.price(2));
						put(37, VPSSlot.builder()
								.name("1 beacon")
								.displayAndGive(Material.BEACON)
								.price(250));
						put(38, VPSSlot.builder()
								.name("1 dragon head")
								.displayAndGive(new ItemBuilder(Material.DRAGON_HEAD))
								.price(50));
						put(39, VPSSlot.builder()
								.name("1 purple shulker box")
								.displayAndGive(Material.PURPLE_SHULKER_BOX)
								.price(80));
						put(40, VPSSlot.builder()
								.name("1 wither skeleton head")
								.displayAndGive(new ItemBuilder(Material.WITHER_SKELETON_SKULL))
								.price(40));
						put(41, VPSSlot.builder()
								.name("1 totem of undying")
								.displayAndGive(Material.TOTEM_OF_UNDYING)
								.price(50));
						put(42, VPSSlot.builder()
								.name("4 ender crystals")
								.displayAndGive(Material.END_CRYSTAL, 4)
								.price(80));
						put(43, VPSSlot.builder()
								.name("1 elytra")
								.displayAndGive(Material.ELYTRA)
								.price(40));
					}}).build());

			add(VPSPage.builder().items(new HashMap<Integer, VPSSlotBuilder>() {{
						put(10, VPSSlot.builder()
								.name("32 Packed Ice")
								.displayAndGive(Material.PACKED_ICE, 32)
								.price(15));
						put(11, VPSSlot.builder()
								.name("32 Glowstone")
								.displayAndGive(Material.GLOWSTONE, 32)
								.price(7));
						put(12, VPSSlot.builder()
								.name("32 Sea Lanterns")
								.displayAndGive(Material.SEA_LANTERN, 32)
								.price(10));
						put(13, VPSSlot.builder()
								.name("1 Sponge")
								.displayAndGive(Material.SPONGE)
								.price(35));
						put(15, VPSSlot.builder()
								.name("4 Oak Saplings")
								.displayAndGive(new ItemStack(Material.OAK_SAPLING, 4))
								.price(2));
						put(16, VPSSlot.builder()
								.name("4 Spruce Saplings")
								.displayAndGive(new ItemStack(Material.SPRUCE_SAPLING, 4))
								.price(8));
						put(24, VPSSlot.builder()
								.name("4 Birch Saplings")
								.displayAndGive(new ItemStack(Material.BIRCH_SAPLING, 4))
								.price(4));
						put(25, VPSSlot.builder()
								.name("4 Jungle Saplings")
								.displayAndGive(new ItemStack(Material.JUNGLE_SAPLING, 4))
								.price(6));
						put(34, VPSSlot.builder()
								.name("4 Acacia Saplings")
								.displayAndGive(new ItemStack(Material.ACACIA_SAPLING, 4))
								.price(4));
						put(43, VPSSlot.builder()
								.name("4 Dark Oak Saplings")
								.displayAndGive(new ItemStack(Material.DARK_OAK_SAPLING, 4))
								.price(6));
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
						put(37, VPSSlot.builder()
								.name("64 Experience Bottles")
								.displayAndGive(Material.EXPERIENCE_BOTTLE, 64)
								.price(15));
						put(38, VPSSlot.builder()
								.name("1 Notch Apple")
								.displayAndGive(new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1))
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
					}}).build());
		}};
	},

	CREATIVE {
		@Getter
		List<VPSPage> pages = new ArrayList<VPSPage>() {{
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
		List<VPSPage> pages = new ArrayList<VPSPage>() {{
			add(VPSPage.builder()
					.rows(5)
					.items(new HashMap<Integer, VPSSlotBuilder>() {{

						put(10, VPSSlot.builder()
								.name("5 dirt")
								.displayAndGive(Material.DIRT, 5)
								.price(10));
						put(11, VPSSlot.builder()
								.name("1 grass")
								.displayAndGive(Material.GRASS, 1)
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
