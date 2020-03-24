package me.pugabyte.bncore.features.votes.vps;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSPage.VPSSlot;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSPage.VPSSlot.VPSSlotBuilder;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
			add(VPSPage.builder()
					.rows(6)
					.items(new HashMap<Integer, VPSSlotBuilder>() {{
						put(11, VPSSlot.builder()
								.name("$250")
								.display(Material.GOLD_NUGGET)
								.price(1)
								.money(250));
						put(12, VPSSlot.builder()
								.name("$2,500")
								.display(Material.GOLD_NUGGET)
								.price(10)
								.money(2500));
						put(13, VPSSlot.builder()
								.name("$5,000")
								.display(Material.GOLD_NUGGET)
								.price(20)
								.money(5000));
						put(14, VPSSlot.builder()
								.name("$10,000")
								.display(Material.GOLD_NUGGET)
								.price(40)
								.money(10000));

						put(20, VPSSlot.builder()
								.name("Coal Protection Stone")
								.display(new ItemStackBuilder(Material.COAL_ORE)
										.lore("Size: &e11x11x11 &3(Radius of 5)", "Do &c/ps about &3for more info")
										.build())
								.price(4)
								.give(Material.COAL_ORE));
						put(21, VPSSlot.builder()
								.name("Lapis Protection Stone")
								.display(new ItemStackBuilder(Material.LAPIS_ORE)
										.lore("Size: &e21x21x21 &3(Radius of 10)", "Do &c/ps about &3for more info")
										.build())
								.price(15)
								.give(Material.LAPIS_ORE));
						put(22, VPSSlot.builder()
								.name("Diamond Protection Stone")
								.display(new ItemStackBuilder(Material.DIAMOND_ORE)
										.lore("Size: &e41x41x41 &3(Radius of 20)", "Do &c/ps about &3for more info")
										.build())
								.price(50)
								.give(Material.DIAMOND_ORE));
						put(23, VPSSlot.builder()
								.name("Emerald Protection Stone")
								.display(new ItemStackBuilder(Material.EMERALD_ORE)
										.lore("Size: &e81x81x81 &3(Radius of 40)", "Do &c/ps about &3for more info")
										.build())
								.price(100)
								.give(Material.EMERALD_ORE));

						put(39, VPSSlot.builder()
								.name("x3 KillerMoney boost for 2 days")
								.display(new ItemStack(Material.DIAMOND_SWORD, 3))
								.consoleCommand("kmboost [player]")
								.price(30)
								.takePoints(false)
								.close(true));

						put(41, VPSSlot.builder()
								.name("Uncraftable Banners")
								.display(new ItemStackBuilder(Material.BANNER).color(ColorType.CYAN)
										.lore("Pre-selected banners or","choose your own!","","&eClick to teleport &3to the",
												"banner display area","","Read the &ehologram&3!","","&6Price: &e5-10vp")
										.build())
								.command("warp banners")
								.close(true));

						put(16, VPSSlot.builder()
								.name("Diamond Horse Armor")
								.display(Material.DIAMOND_BARDING)
								.price(10)
								.give(Material.DIAMOND_BARDING));

						put(25, VPSSlot.builder()
								.name("Iron Horse Armor")
								.display(Material.IRON_BARDING)
								.price(6)
								.give(Material.IRON_BARDING));

						put(34, VPSSlot.builder()
								.name("Gold Horse Armor")
								.display(Material.GOLD_BARDING)
								.price(8)
								.give(Material.GOLD_BARDING));

						put(43, VPSSlot.builder()
								.name("Saddle")
								.display(Material.SADDLE)
								.price(10)
								.give(Material.SADDLE));

						put(17, VPSSlot.builder()
								.name("Full Diamond Armor Set")
								.display(Material.DIAMOND_CHESTPLATE)
								.price(28)
								.give(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS));

						put(26, VPSSlot.builder()
								.name("Full Iron Armor Set")
								.display(Material.IRON_CHESTPLATE)
								.price(18)
								.give(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS));

						put(35, VPSSlot.builder()
								.name("Full Golden Armor Set")
								.display(Material.GOLD_CHESTPLATE)
								.price(15)
								.give(Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS));

						put(44, VPSSlot.builder()
								.name("Full Chainmail Armor Set")
								.display(Material.CHAINMAIL_CHESTPLATE)
								.price(20)
								.give(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS));

					}}).build());

			// Page 2
			add(VPSPage.builder()
					.rows(6)
					.items(new HashMap<Integer, VPSSlotBuilder>() {{
						put(38, VPSSlot.builder()
								.name("10 Emeralds")
								.display(Material.EMERALD, 10)
								.price(12)
								.give(Material.EMERALD, 10));
						put(39, VPSSlot.builder()
								.name("10 Diamonds")
								.display(Material.DIAMOND, 10)
								.price(15)
								.give(Material.DIAMOND, 10));
						put(40, VPSSlot.builder()
								.name("10 Gold Ingots")
								.display(Material.GOLD_INGOT, 10)
								.price(7)
								.give(Material.GOLD_INGOT, 10));
						put(41, VPSSlot.builder()
								.name("10 Iron Ingots")
								.display(Material.IRON_INGOT, 10)
								.price(5)
								.give(Material.IRON_INGOT, 10));
						put(42, VPSSlot.builder()
								.name("10 Lapis Lazuli")
								.display(new ItemStackBuilder(Material.INK_SACK).durability((byte) 9).build())
								.price(3)
								.give(new ItemStackBuilder(Material.INK_SACK).durability((byte) 9).build()));
						put(43, VPSSlot.builder()
								.name("16 Quartz")
								.display(Material.QUARTZ)
								.price(2)
								.give(Material.QUARTZ, 16));
						put(44, VPSSlot.builder()
								.name("32 Coal")
								.display(Material.COAL)
								.price(2)
								.give(Material.COAL, 32));
						put(12, VPSSlot.builder()
								.name("Frost Walker 2")
								.display(new ItemStackBuilder(Material.ENCHANTED_BOOK).build())
								.price(30)
								.give(new ItemStackBuilder(Material.ENCHANTED_BOOK)
										.enchant(Enchantment.FROST_WALKER, 2)
										.build()));
						put(13, VPSSlot.builder()
								.name("Unbreaking 3")
								.display(new ItemStackBuilder(Material.ENCHANTED_BOOK).build())
								.price(30)
								.give(new ItemStackBuilder(Material.ENCHANTED_BOOK)
										.enchant(Enchantment.DURABILITY, 3)
										.build()));
						put(14, VPSSlot.builder()
								.name("Mending 1")
								.display(new ItemStackBuilder(Material.ENCHANTED_BOOK).build())
								.price(45)
								.give(new ItemStackBuilder(Material.ENCHANTED_BOOK)
										.enchant(Enchantment.MENDING, 1)
										.build()));
						put(15, VPSSlot.builder()
								.name("Sweeping Edge 3")
								.display(new ItemStackBuilder(Material.ENCHANTED_BOOK).build())
								.price(35)
								.give(new ItemStackBuilder(Material.ENCHANTED_BOOK)
										.enchant(Enchantment.SWEEPING_EDGE, 3)
										.build()));
						put(16, VPSSlot.builder()
								.name("Infinity 1")
								.display(new ItemStackBuilder(Material.ENCHANTED_BOOK).build())
								.price(40)
								.give(new ItemStackBuilder(Material.ENCHANTED_BOOK)
										.enchant(Enchantment.ARROW_INFINITE, 1)
										.build()));
						put(23, VPSSlot.builder()
								.name("Thorns 1")
								.display(new ItemStackBuilder(Material.ENCHANTED_BOOK).build())
								.price(15)
								.give(new ItemStackBuilder(Material.ENCHANTED_BOOK)
										.enchant(Enchantment.THORNS, 1)
										.build()));

					}}).build());

			// Page 3
			add(VPSPage.builder()
					.rows(6)
					.items(new HashMap<Integer, VPSSlotBuilder>() {{
						put(11, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(12, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(13, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(14, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(15, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(16, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(17, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(21, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(22, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(23, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(24, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(25, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(32, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(38, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(39, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(40, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(41, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(42, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(43, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
						put(44, VPSSlot.builder()
								.name("")
								.display()
								.price()
								.give());
					}}).build());

			// Page 4
			add(VPSPage.builder()
					.rows(6)
					.items(new HashMap<Integer, VPSSlotBuilder>() {{
						put(11, VPSSlot.builder()
								.name("32 Packed Ice")
								.display(Material.PACKED_ICE, 32)
								.price(15)
								.give(Material.PACKED_ICE, 32));
						put(12, VPSSlot.builder()
								.name("32 Glowstone")
								.display(Material.GLOWSTONE, 32)
								.price(7)
								.give(Material.GLOWSTONE, 32));
						put(13, VPSSlot.builder()
								.name("32 Sea Lanterns")
								.display()
								.price(10)
								.give());
						put(14, VPSSlot.builder()
								.name("1 Sponge")
								.display()
								.price(35)
								.give());
						put(16, VPSSlot.builder()
								.name("4 Oak Saplings")
								.display()
								.price(2)
								.give());
						put(17, VPSSlot.builder()
								.name("4 Spruce Saplings")
								.display()
								.price(8)
								.give());
						put(25, VPSSlot.builder()
								.name("4 Birch Saplings")
								.display()
								.price(4)
								.give());
						put(26, VPSSlot.builder()
								.name("4 Jungle Saplings")
								.display()
								.price(6)
								.give());
						put(35, VPSSlot.builder()
								.name("4 Acacia Saplings")
								.display()
								.price(4)
								.give());
						put(44, VPSSlot.builder()
								.name("4 Dark Oak Saplings")
								.display()
								.price(6)
								.give());
						put(29, VPSSlot.builder()
								.name("4 Blaze Rods")
								.display()
								.price(8)
								.give());
						put(30, VPSSlot.builder()
								.name("8 Ender Pearls")
								.display()
								.price(2)
								.give());
						put(31, VPSSlot.builder()
								.name("4 Slimeballs")
								.display()
								.price(5)
								.give());
						put(32, VPSSlot.builder()
								.name("16 Gunpowder")
								.display()
								.price(2)
								.give());
						put(33, VPSSlot.builder()
								.name("16 Leather")
								.display()
								.price(10)
								.give());
						put(38, VPSSlot.builder()
								.name("64 Experience Bottles")
								.display()
								.price(15)
								.give());
						put(39, VPSSlot.builder()
								.name("1 Notch Apple")
								.display()
								.price(30)
								.give());
						put(40, VPSSlot.builder()
								.name("1 Golden Apple")
								.display()
								.price(10)
								.give());
						put(41, VPSSlot.builder()
								.name("16 Bones")
								.display()
								.price(2)
								.give());
						put(42, VPSSlot.builder()
								.name("4 Steak")
								.display()
								.price(1)
								.give());
					}}).build());

		}};
	},
	CREATIVE {
		@Getter
		List<VPSPage> pages = new ArrayList<VPSPage>() {{

			// Page 1
			add(VPSPage.builder()
					.rows(6)
					.items(new HashMap<Integer, VPSSlotBuilder>() {{

					}}).build());
		}};
	},
	SKYBLOCK {
		@Getter
		List<VPSPage> pages = new ArrayList<VPSPage>() {{

			// Page 1
			add(VPSPage.builder()
					.rows(6)
					.items(new HashMap<Integer, VPSSlotBuilder>() {{

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
			this.rows = rows;
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
