package me.pugabyte.bncore.features.votes.vps;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSPage.VPSSlot;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSPage.VPSSlot.VPSSlotBuilder;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.TreeSpecies;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
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
								.display(Material.GOLD_NUGGET)
								.price(1)
								.money(250));
						put(11, VPSSlot.builder()
								.name("$2,500")
								.display(Material.GOLD_NUGGET)
								.price(10)
								.money(2500));
						put(12, VPSSlot.builder()
								.name("$5,000")
								.display(Material.GOLD_NUGGET)
								.price(20)
								.money(5000));
						put(13, VPSSlot.builder()
								.name("$10,000")
								.display(Material.GOLD_NUGGET)
								.price(40)
								.money(10000));
						put(19, VPSSlot.builder()
								.name("Coal Protection Stone")
								.display(new ItemStackBuilder(Material.COAL_ORE)
										.lore("Size: &e11x11x11 &3(Radius of 5)", "Do &c/ps about &3for more info")
										.build())
								.price(4)
								.give(Material.COAL_ORE));
						put(20, VPSSlot.builder()
								.name("Lapis Protection Stone")
								.display(new ItemStackBuilder(Material.LAPIS_ORE)
										.lore("Size: &e21x21x21 &3(Radius of 10)", "Do &c/ps about &3for more info")
										.build())
								.price(15)
								.give(Material.LAPIS_ORE));
						put(21, VPSSlot.builder()
								.name("Diamond Protection Stone")
								.display(new ItemStackBuilder(Material.DIAMOND_ORE)
										.lore("Size: &e41x41x41 &3(Radius of 20)", "Do &c/ps about &3for more info")
										.build())
								.price(50)
								.give(Material.DIAMOND_ORE));
						put(22, VPSSlot.builder()
								.name("Emerald Protection Stone")
								.display(new ItemStackBuilder(Material.EMERALD_ORE)
										.lore("Size: &e81x81x81 &3(Radius of 40)", "Do &c/ps about &3for more info")
										.build())
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
								.display(new ItemStackBuilder(Material.BANNER).durability(ColorType.CYAN.getDyeColor().getDyeData())
										.lore("Pre-selected banners or","choose your own!","","&eClick to teleport &3to the",
												"banner display area","","Read the &ehologram&3!","","&6Price: &e5-10vp")
										.build())
								.command("warp banners")
								.close(true));
						put(15, VPSSlot.builder()
								.name("Diamond Horse Armor")
								.display(Material.DIAMOND_BARDING)
								.price(10)
								.give(Material.DIAMOND_BARDING));
						put(24, VPSSlot.builder()
								.name("Iron Horse Armor")
								.display(Material.IRON_BARDING)
								.price(6)
								.give(Material.IRON_BARDING));
						put(33, VPSSlot.builder()
								.name("Gold Horse Armor")
								.display(Material.GOLD_BARDING)
								.price(8)
								.give(Material.GOLD_BARDING));
						put(42, VPSSlot.builder()
								.name("Saddle")
								.display(Material.SADDLE)
								.price(10)
								.give(Material.SADDLE));
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
								.display(Material.GOLD_CHESTPLATE)
								.price(15)
								.give(Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS));
						put(43, VPSSlot.builder()
								.name("Full Chainmail Armor Set")
								.display(Material.CHAINMAIL_CHESTPLATE)
								.price(20)
								.give(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS));
					}}).build());

			add(VPSPage.builder().items(new HashMap<Integer, VPSSlotBuilder>() {{
						put(37, VPSSlot.builder()
								.name("10 Emeralds")
								.display(Material.EMERALD, 10)
								.price(12)
								.give(Material.EMERALD, 10));
						put(38, VPSSlot.builder()
								.name("10 Diamonds")
								.display(Material.DIAMOND, 10)
								.price(15)
								.give(Material.DIAMOND, 10));
						put(39, VPSSlot.builder()
								.name("10 Gold Ingots")
								.display(Material.GOLD_INGOT, 10)
								.price(7)
								.give(Material.GOLD_INGOT, 10));
						put(40, VPSSlot.builder()
								.name("10 Iron Ingots")
								.display(Material.IRON_INGOT, 10)
								.price(5)
								.give(Material.IRON_INGOT, 10));
						put(41, VPSSlot.builder()
								.name("10 Lapis Lazuli")
								.display(new ItemStack(Material.INK_SACK, 10, ColorType.BLUE.getDyeColor().getDyeData()))
								.price(3)
								.give(new ItemStack(Material.INK_SACK, 10, ColorType.BLUE.getDyeColor().getDyeData())));
						put(42, VPSSlot.builder()
								.name("16 Quartz")
								.display(Material.QUARTZ, 16)
								.price(2)
								.give(Material.QUARTZ, 16));
						put(43, VPSSlot.builder()
								.name("32 Coal")
								.display(Material.COAL, 32)
								.price(2)
								.give(Material.COAL, 32));
						put(11, VPSSlot.builder()
								.name("Frost Walker 2")
								.display(new ItemStackBuilder(Material.ENCHANTED_BOOK).build())
								.price(30)
								.give(new ItemStackBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.FROST_WALKER, 2)));
						put(12, VPSSlot.builder()
								.name("Unbreaking 3")
								.display(new ItemStackBuilder(Material.ENCHANTED_BOOK).build())
								.price(30)
								.give(new ItemStackBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.DURABILITY, 3)));
						put(13, VPSSlot.builder()
								.name("Mending 1")
								.display(new ItemStackBuilder(Material.ENCHANTED_BOOK).build())
								.price(45)
								.give(new ItemStackBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.MENDING, 1)));
						put(14, VPSSlot.builder()
								.name("Sweeping Edge 3")
								.display(new ItemStackBuilder(Material.ENCHANTED_BOOK).build())
								.price(35)
								.give(new ItemStackBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.SWEEPING_EDGE, 3)));
						put(15, VPSSlot.builder()
								.name("Infinity 1")
								.display(new ItemStackBuilder(Material.ENCHANTED_BOOK).build())
								.price(40)
								.give(new ItemStackBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.ARROW_INFINITE, 1)));
						put(22, VPSSlot.builder()
								.name("Thorns 1")
								.display(new ItemStackBuilder(Material.ENCHANTED_BOOK).build())
								.price(15)
								.give(new ItemStackBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.THORNS, 1)));
					}}).build());

			add(VPSPage.builder().items(new HashMap<Integer, VPSSlotBuilder>() {{
						put(10, VPSSlot.builder()
								.name("2 wolf spawn eggs")
								.display(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.WOLF))
								.price(25)
								.give(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.WOLF)));
						put(11, VPSSlot.builder()
								.name("2 ocelot spawn eggs")
								.display(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.OCELOT))
								.price(35)
								.give(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.OCELOT)));
						put(12, VPSSlot.builder()
								.name("2 horse spawn eggs")
								.display(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.HORSE))
								.price(100)
								.give(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.HORSE)));
						put(13, VPSSlot.builder()
								.name("2 donkey spawn eggs")
								.display(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.DONKEY))
								.price(100)
								.give(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.DONKEY)));
						put(14, VPSSlot.builder()
								.name("2 llama spawn eggs")
								.display(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.LLAMA))
								.price(100)
								.give(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.LLAMA)));
						put(15, VPSSlot.builder()
								.name("2 cow spawn eggs")
								.display(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.COW))
								.price(150)
								.give(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.COW)));
						put(16, VPSSlot.builder()
								.name("2 sheep spawn eggs")
								.display(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.SHEEP))
								.price(100)
								.give(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.SHEEP)));
						put(20, VPSSlot.builder()
								.name("2 bunny spawn eggs")
								.display(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.RABBIT))
								.price(50)
								.give(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.RABBIT)));
						put(21, VPSSlot.builder()
								.name("2 villager spawn eggs")
								.display(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.VILLAGER))
								.price(200)
								.give(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.VILLAGER)));
						put(22, VPSSlot.builder()
								.name("2 mooshroom spawn eggs")
								.display(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.MUSHROOM_COW))
								.price(200)
								.give(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.MUSHROOM_COW)));
						put(23, VPSSlot.builder()
								.name("2 parrot spawn eggs")
								.display(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.PARROT))
								.price(75)
								.give(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.PARROT)));
						put(24, VPSSlot.builder()
								.name("2 pig spawn eggs")
								.display(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.PIG))
								.price(50)
								.give(new ItemStackBuilder(Material.MONSTER_EGG).amount(2).spawnEgg(EntityType.PIG)));
						put(31, VPSSlot.builder()
								.name("1 name tag")
								.display(Material.NAME_TAG)
								.price(2)
								.give(Material.NAME_TAG));
						put(37, VPSSlot.builder()
								.name("1 beacon")
								.display(Material.BEACON)
								.price(250)
								.give(Material.BEACON));
						put(38, VPSSlot.builder()
								.name("1 dragon head")
								.display(new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.DRAGON.ordinal()))
								.price(50)
								.give(new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.DRAGON.ordinal())));
						put(39, VPSSlot.builder()
								.name("1 purple shulker box")
								.display(Material.PURPLE_SHULKER_BOX)
								.price(80)
								.give(Material.PURPLE_SHULKER_BOX));
						put(40, VPSSlot.builder()
								.name("1 wither skeleton head")
								.display(new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.SKELETON.ordinal()))
								.price(40)
								.give(new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.SKELETON.ordinal())));
						put(41, VPSSlot.builder()
								.name("1 totem of undying")
								.display(Material.TOTEM)
								.price(50)
								.give(Material.TOTEM));
						put(42, VPSSlot.builder()
								.name("4 ender crystals")
								.display(Material.END_CRYSTAL, 4)
								.price(80)
								.give(Material.END_CRYSTAL, 4));
						put(43, VPSSlot.builder()
								.name("1 elytra")
								.display(Material.ELYTRA)
								.price(40)
								.give(Material.ELYTRA));
					}}).build());

			add(VPSPage.builder().items(new HashMap<Integer, VPSSlotBuilder>() {{
						put(10, VPSSlot.builder()
								.name("32 Packed Ice")
								.display(Material.PACKED_ICE, 32)
								.price(15)
								.give(Material.PACKED_ICE, 32));
						put(11, VPSSlot.builder()
								.name("32 Glowstone")
								.display(Material.GLOWSTONE, 32)
								.price(7)
								.give(Material.GLOWSTONE, 32));
						put(12, VPSSlot.builder()
								.name("32 Sea Lanterns")
								.display(Material.SEA_LANTERN, 32)
								.price(10)
								.give(Material.SEA_LANTERN, 32));
						put(13, VPSSlot.builder()
								.name("1 Sponge")
								.display(Material.SPONGE)
								.price(35)
								.give(Material.SPONGE));
						put(15, VPSSlot.builder()
								.name("4 Oak Saplings")
								.display(new ItemStack(Material.SAPLING, 4, TreeSpecies.GENERIC.getData()))
								.price(2)
								.give(new ItemStack(Material.SAPLING, 4, TreeSpecies.GENERIC.getData())));
						put(16, VPSSlot.builder()
								.name("4 Spruce Saplings")
								.display(new ItemStack(Material.SAPLING, 4, TreeSpecies.REDWOOD.getData()))
								.price(8)
								.give(new ItemStack(Material.SAPLING, 4, TreeSpecies.REDWOOD.getData())));
						put(24, VPSSlot.builder()
								.name("4 Birch Saplings")
								.display(new ItemStack(Material.SAPLING, 4, TreeSpecies.BIRCH.getData()))
								.price(4)
								.give(new ItemStack(Material.SAPLING, 4, TreeSpecies.BIRCH.getData())));
						put(25, VPSSlot.builder()
								.name("4 Jungle Saplings")
								.display(new ItemStack(Material.SAPLING, 4, TreeSpecies.JUNGLE.getData()))
								.price(6)
								.give(new ItemStack(Material.SAPLING, 4, TreeSpecies.JUNGLE.getData())));
						put(34, VPSSlot.builder()
								.name("4 Acacia Saplings")
								.display(new ItemStack(Material.SAPLING, 4, TreeSpecies.ACACIA.getData()))
								.price(4)
								.give(new ItemStack(Material.SAPLING, 4, TreeSpecies.ACACIA.getData())));
						put(43, VPSSlot.builder()
								.name("4 Dark Oak Saplings")
								.display(new ItemStack(Material.SAPLING, 4, TreeSpecies.DARK_OAK.getData()))
								.price(6)
								.give(new ItemStack(Material.SAPLING, 4, TreeSpecies.DARK_OAK.getData())));
						put(28, VPSSlot.builder()
								.name("4 Blaze Rods")
								.display(Material.BLAZE_ROD, 4)
								.price(8)
								.give(Material.BLAZE_ROD, 4));
						put(29, VPSSlot.builder()
								.name("8 Ender Pearls")
								.display(Material.ENDER_PEARL, 8)
								.price(2)
								.give(Material.ENDER_PEARL, 8));
						put(30, VPSSlot.builder()
								.name("4 Slimeballs")
								.display(Material.SLIME_BALL, 4)
								.price(5)
								.give(Material.SLIME_BALL, 4));
						put(31, VPSSlot.builder()
								.name("16 Gunpowder")
								.display(Material.SULPHUR, 16)
								.price(2)
								.give(Material.SULPHUR, 16));
						put(32, VPSSlot.builder()
								.name("16 Leather")
								.display(Material.LEATHER, 16)
								.price(10)
								.give(Material.LEATHER, 16));
						put(37, VPSSlot.builder()
								.name("64 Experience Bottles")
								.display(Material.EXP_BOTTLE, 64)
								.price(15)
								.give(Material.EXP_BOTTLE, 64));
						put(38, VPSSlot.builder()
								.name("1 Notch Apple")
								.display(new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1))
								.price(30)
								.give(new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1)));
						put(39, VPSSlot.builder()
								.name("1 Golden Apple")
								.display(Material.GOLDEN_APPLE)
								.price(10)
								.give(Material.GOLDEN_APPLE));
						put(40, VPSSlot.builder()
								.name("16 Bones")
								.display(Material.BONE, 16)
								.price(2)
								.give(Material.BONE, 16));
						put(41, VPSSlot.builder()
								.name("4 Steak")
								.display(Material.COOKED_BEEF, 4)
								.price(1)
								.give(Material.COOKED_BEEF, 4));
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
								.display(new ItemStackBuilder(Material.STEP).durability(1)
										.lore("You may purchase up to","&efour &3addition plots either",
												"from the VPS or the server","store (&c/donate&3)","","&6Price: &e150vp"))
								.command("vps buy plot"));
						put(13, VPSSlot.builder()
								.name("Player Heads")
								.display(new ItemStackBuilder(Material.SKULL_ITEM).durability(SkullType.PLAYER.ordinal())
										.lore("&6Price &3(Non-Staff): &e6vp","&6Price &3(Staff): &e9vp"))
								.command("vps buy head")
								.close(true));
						put(15, VPSSlot.builder()
								.name("Uncraftable Banners")
								.display(new ItemStackBuilder(Material.BANNER).durability(ColorType.CYAN.getDyeColor().getDyeData())
										.lore("&eClick to teleport &3to the","banner display area","","Read the &ehologram&3!","","&6Price: &e5-10vp"))
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
								.display(Material.DIRT, 5)
								.price(10)
								.give(Material.DIRT, 5));
						put(11, VPSSlot.builder()
								.name("1 grass")
								.display(Material.GRASS, 1)
								.price(5)
								.give(Material.GRASS, 1));
						put(12, VPSSlot.builder()
								.name("64 cobble")
								.display(Material.COBBLESTONE, 64)
								.price(5)
								.give(Material.COBBLESTONE, 64));
						put(14, VPSSlot.builder()
								.name("1 water bucket")
								.display(Material.WATER_BUCKET)
								.price(25)
								.give(Material.WATER_BUCKET));
						put(15, VPSSlot.builder()
								.name("1 lava bucket")
								.display(Material.LAVA_BUCKET)
								.price(35)
								.give(Material.LAVA_BUCKET));
						put(16, VPSSlot.builder()
								.name("1 iron pickaxe")
								.display(Material.IRON_PICKAXE)
								.price(10)
								.give(Material.IRON_PICKAXE));
						put(28, VPSSlot.builder()
								.name("5 seeds")
								.display(Material.SEEDS, 5)
								.price(5)
								.give(Material.SEEDS, 5));
						put(29, VPSSlot.builder()
								.name("1 pumpkin seeds")
								.display(Material.PUMPKIN_SEEDS)
								.price(3)
								.give(Material.PUMPKIN_SEEDS));
						put(30, VPSSlot.builder()
								.name("1 melon seeds")
								.display(Material.MELON_SEEDS)
								.price(3)
								.give(Material.MELON_SEEDS));
						put(31, VPSSlot.builder()
								.name("3 bone meal")
								.display(new ItemStack(Material.INK_SACK, 1, DyeColor.WHITE.getDyeData()))
								.price(1)
								.give(new ItemStack(Material.INK_SACK, 1, DyeColor.WHITE.getDyeData())));
						put(32, VPSSlot.builder()
								.name("8 bread")
								.display(Material.BREAD, 8)
								.price(3)
								.give(Material.BREAD, 8));
						put(33, VPSSlot.builder()
								.name("16 torches")
								.display(Material.TORCH, 16)
								.price(5)
								.give(Material.TORCH, 16));
						put(34, VPSSlot.builder()
								.name("Reset your deaths to 0")
								.display(Material.TOTEM)
								.price(40)
								.consoleCommand("asadmin setdeaths [player] 0"));
					}}).build());
		}};
	};

	public abstract List<VPSPage> getPages();

	public VPSPage getPage(int page) {
		return getPages().get(page - 1);
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
			private String name;
			private ItemStack display;

			private int price;
			private boolean takePoints;
			private boolean close;

			private int money;
			private List<ItemStack> items;
			private String command;
			private String consoleCommand;

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

				public VPSSlotBuilder display(Material display, int amount) {
					return display(new ItemStack(display, amount));
				}

				public VPSSlotBuilder display(Material display) {
					return display(new ItemStack(display));
				}

				public VPSSlotBuilder display(ItemStackBuilder display) {
					return display(display.build());
				}

				public VPSSlotBuilder display(ItemStack display) {
					this.display = display;
					return this;
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

				public VPSSlotBuilder give(Material material, int amount) {
					return give(new ItemStack(material, amount));
				}

				public VPSSlotBuilder give(ItemStackBuilder builder) {
					return give(builder.build());
				}

				public VPSSlotBuilder give(Material... materials) {
					return give(Arrays.stream(materials).map(ItemStack::new).collect(Collectors.toList()));
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
					return new VPSSlot(this.name, this.display, this.price, this.takePoints, this.close, this.money, this.items, this.command, this.consoleCommand);
				}
			}
		}
	}
}
