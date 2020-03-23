package me.pugabyte.bncore.features.votes.vps;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSPage.VPSSlot;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum VPSMenu {
	SURVIVAL {
		@Getter
		List<VPSPage> pages = new ArrayList<VPSPage>() {{
			add(VPSPage.builder()
					.rows(6)
					.items(new HashMap<Integer, VPSSlot>() {{
						put(11, VPSSlot.builder()
								.name("$250")
								.display(Material.GOLD_NUGGET)
								.price(1)
								.money(250)
								.build());
						put(12, VPSSlot.builder()
								.name("$2,500")
								.display(Material.GOLD_NUGGET)
								.price(10)
								.money(2500)
								.build());
						put(13, VPSSlot.builder()
								.name("$5,000")
								.display(Material.GOLD_NUGGET)
								.price(20)
								.money(5000)
								.build());
						put(14, VPSSlot.builder()
								.name("$10,000")
								.display(Material.GOLD_NUGGET)
								.price(40)
								.money(10000)
								.build());

						put(20, VPSSlot.builder()
								.name("Coal Protection Stone")
								.display(Material.COAL_ORE)
								.price(4)
								.item(new ItemStackBuilder(Material.COAL_ORE)
										.lore("Size: &e11x11x11 &3(Radius of 5)", "Do &c/ps about &3for more info")
										.build())
								.build());
						put(21, VPSSlot.builder()
								.name("Lapis Protection Stone")
								.display(Material.LAPIS_ORE)
								.price(15)
								.item(new ItemStackBuilder(Material.LAPIS_ORE)
										.lore("Size: &e21x21x21 &3(Radius of 10)", "Do &c/ps about &3for more info")
										.build())
								.build());
						put(22, VPSSlot.builder()
								.name("Diamond Protection Stone")
								.display(Material.DIAMOND_ORE)
								.price(50)
								.item(new ItemStackBuilder(Material.DIAMOND_ORE)
										.lore("Size: &e41x41x41 &3(Radius of 20)", "Do &c/ps about &3for more info")
										.build())
								.build());
						put(23, VPSSlot.builder()
								.name("Emerald Protection Stone")
								.display(Material.EMERALD_ORE)
								.price(100)
								.item(new ItemStackBuilder(Material.EMERALD_ORE)
										.lore("Size: &e81x81x81 &3(Radius of 40)", "Do &c/ps about &3for more info")
										.build())
								.build());

					}}).build());

		}};
	},
	CREATIVE {
		@Getter
		List<VPSPage> pages = new ArrayList<VPSPage>() {{
			add(VPSPage.builder()
					.rows(6)
					.items(new HashMap<Integer, VPSSlot>() {{

					}}).build());
		}};
	},
	SKYBLOCK {
		@Getter
		List<VPSPage> pages = new ArrayList<VPSPage>() {{
			add(VPSPage.builder()
					.rows(6)
					.items(new HashMap<Integer, VPSSlot>() {{

					}}).build());
		}};
	};

	public abstract List<VPSPage> getPages();

	public VPSPage getPage(int page) {
		return getPages().get(page - 1);
	}

	@Data
	@Builder
	public static class VPSPage {
		@Default
		private int rows = 6;
		private Map<Integer, VPSSlot> items;

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

				VPSSlotBuilder() {}

				public VPSSlotBuilder name(String name) {
					this.name = name;
					return this;
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

				public VPSSlotBuilder item(Material material) {
					return item(new ItemStack(material));
				}

				public VPSSlotBuilder item(ItemStack itemStack) {
					return items(Collections.singletonList(itemStack));
				}

				public VPSSlotBuilder items(Material... materials) {
					return items(Arrays.stream(materials).map(ItemStack::new).collect(Collectors.toList()));
				}

				public VPSSlotBuilder items(ItemStack... items) {
					return items(Arrays.stream(items).collect(Collectors.toList()));
				}

				public VPSSlotBuilder items(List<ItemStack> items) {
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
