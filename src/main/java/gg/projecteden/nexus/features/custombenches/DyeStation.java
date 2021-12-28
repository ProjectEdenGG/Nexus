package gg.projecteden.nexus.features.custombenches;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.ItemClickData;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DyeStation extends CustomBench {

	private static final String usageLore = "&3Used in Dye Station";
	private static final int maxUses = 5;
	private static final String usesLore = "&3Uses: &e";

	private static final int dyeModelData = 5;
	private static final ItemBuilder magicDye = new ItemBuilder(Material.PAPER)
		.customModelData(dyeModelData)
		.name(Gradient.of(List.of(ChatColor.RED, ChatColor.YELLOW, ChatColor.AQUA)).apply("Magic Dye"))
		.lore(usageLore, usesLore + maxUses);

	private static final int stainModelData = 6;
	private static final ItemBuilder magicStain = new ItemBuilder(Material.PAPER)
		.customModelData(stainModelData)
		.name(Gradient.of(List.of(ChatColor.of("#e0a175"), ChatColor.of("#5c371d"))).apply("Magic Stain"))
		.lore(usageLore, usesLore + maxUses);

	private static final ItemBuilder dyeStation = new ItemBuilder(Material.CRAFTING_TABLE)
		.customModelData(1)
		.name("Dye Station");


	public static ItemBuilder getMagicDye() {
		return magicDye.clone();
	}

	public static ItemBuilder getMagicStain() {
		return magicStain.clone();
	}

	public static ItemBuilder getDyeStation() {
		return dyeStation.clone();
	}

	@Override
	CustomBenchType getBenchType() {
		return CustomBenchType.DYE_STATION;
	}

	public static void open(Player player) {
		new DyeStationMenu().open(player);
	}

	public static void openCheat(Player player) {
		new DyeStationMenu().openCheat(player);
	}

	@AllArgsConstructor
	public enum DyeType {
		DYE(dyeModelData),
		STAIN(stainModelData),
		;

		@Getter
		int modelData;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	private static class DyeStationMenu extends MenuUtils implements InventoryProvider, Listener {
		Player player;
		boolean cheatMode = false;
		DyeType dyeType;
		ItemStack dyeable;
		ItemStack dye;
		ItemStack result;
		DyeChoice dyeChoice;
		Color color;

		private static final SlotPos slot_dyeable = new SlotPos(1, 1);
		private static final SlotPos slot_dye = new SlotPos(3, 1);
		private static final SlotPos slot_result = new SlotPos(2, 7);
		private static final SlotPos slot_cheat_dye = new SlotPos(0, 3);
		private static final SlotPos slot_cheat_stain = new SlotPos(0, 5);


		public void open(Player player) {
			open(player, false, null, null, null, null, null, null);
		}

		public void openCheat(Player player) {
			open(player, true, null, null, null, null, null, null);
		}

		public void open(Player player, boolean staffMode, DyeType dyeType, ItemStack dyeable, ItemStack dye, ItemStack result, DyeChoice dyeChoice, Color color) {
			this.player = player;
			this.cheatMode = staffMode;

			this.dyeType = dyeType;
			if (dyeType == null)
				this.dyeType = DyeType.DYE;

			this.dyeable = dyeable;
			if (dyeable == null)
				this.dyeable = new ItemStack(Material.AIR);

			this.dye = dye;
			if (dye == null) {
				this.dye = new ItemStack(Material.AIR);
				if (staffMode)
					this.dye = getMagicDye().build();
			}

			this.result = result;
			if (result == null)
				this.result = new ItemStack(Material.AIR);

			this.dyeChoice = dyeChoice;
			this.color = color;

			getInv(this.player, this.cheatMode, this.dyeType, this.dyeable, this.dye, this.result, this.dyeChoice, this.color).open(this.player);
		}

		private SmartInventory getInv(Player player, boolean staffMode, DyeType dyeType, ItemStack dyeable, ItemStack dye, ItemStack result, DyeChoice dyeChoice, Color color) {
			return SmartInventory.builder()
				.title(StringUtils.colorize("&fꈉ섈"))
				.size(6, 9)
				.provider(new DyeStationMenu(player, staffMode, dyeType, dyeable, dye, result, dyeChoice, color))
				.closeable(false)
				.build();
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			contents.set(0, ClickableItem.from(closeItem(), e -> {
				contents.get(slot_dyeable).ifPresent(clickableItem -> PlayerUtils.giveItem(player, clickableItem.getItem()));
				contents.get(slot_dye).ifPresent(clickableItem -> {
					if (!cheatMode)
						PlayerUtils.giveItem(player, clickableItem.getItem());
				});

				getInv(player, cheatMode, dyeType, dyeable, dye, result, dyeChoice, color).close(player);
			}));

			contents.set(slot_dyeable, ClickableItem.from(dyeable, e -> replaceItem(player, contents, e, slot_dyeable)));
			contents.set(slot_dye, ClickableItem.from(dye, e -> replaceItem(player, contents, e, slot_dye)));
			if (color != null)
				setResultItem(color, contents);

			fillColors(dyeType, contents);

			if (dyeType.equals(DyeType.DYE) && dyeChoice != null)
				fillChoices(dyeChoice, contents);

			if (cheatMode) {
				contents.set(
					slot_cheat_dye,
					ClickableItem.from(getMagicDye().resetLore().build(),
						e -> setDyeItem(contents, getMagicDye().build())));
				contents.set(slot_cheat_stain,
					ClickableItem.from(getMagicStain().resetLore().build(),
						e -> setDyeItem(contents, getMagicStain().build())));
			}
		}

		private void setDyeItem(InventoryContents contents, ItemStack item) {
			contents.set(slot_dye, ClickableItem.empty(item));
			reopenMenu(contents);
		}

		private void fillColors(DyeType dyeType, InventoryContents contents) {
			int row = 1;
			int col = 3;
			int count = 0;

			switch (dyeType) {
				case DYE -> {
					for (DyeChoice _dyeChoice : DyeChoice.values()) {
						String itemName = StringUtils.camelCase(_dyeChoice) + "s";
						contents.set(row, col++, ClickableItem.from(_dyeChoice.getItem(itemName), e -> fillChoices(_dyeChoice, contents)));

						if (++count == 3) {
							++row;
							col = 3;
							count = 0;
						}
					}
				}

				case STAIN -> {
					for (StainChoice _stainChoice : StainChoice.values()) {
						String itemName = StringUtils.camelCase(_stainChoice);
						Color color = _stainChoice.getButton().getColor();
						contents.set(row, col++, ClickableItem.from(_stainChoice.getItem(itemName), e -> setResultItem(color, contents)));

						if (++count == 3) {
							++row;
							col = 3;
							count = 0;
						}
					}
				}
			}
		}

		private void replaceItem(Player player, InventoryContents contents, ItemClickData e, SlotPos slot) {
			ItemStack cursorItem = player.getItemOnCursor();
			boolean emptyCursor = ItemUtils.isNullOrAir(cursorItem);
			ItemStack slotItem = e.getItem();

			if (ItemUtils.isNullOrAir(slotItem) && emptyCursor)
				return;

			// if slot is empty
			if (ItemUtils.isNullOrAir(slotItem)) {
				contents.set(slot, ClickableItem.empty(cursorItem));
				player.setItemOnCursor(null);

				contents.set(slot_result, ClickableItem.empty(result));
				reopenMenu(contents);

				// if slot is not empty
			} else {
				if (emptyCursor) {
					contents.set(slot, ClickableItem.empty(new ItemStack(Material.AIR)));
					contents.set(slot_result, ClickableItem.empty(new ItemStack(Material.AIR)));
				} else {
					contents.set(slot, ClickableItem.empty(cursorItem));
					player.setItemOnCursor(null);
				}

				reopenMenu(contents);
				player.setItemOnCursor(slotItem);
			}
		}

		private void reopenMenu(InventoryContents contents) {
			Optional<ClickableItem> itemOptional = contents.get(slot_dyeable);
			ItemStack _dyeable = itemOptional.map(ClickableItem::getItem).orElse(null);

			Optional<ClickableItem> dyeOptional = contents.get(slot_dye);
			ItemStack _dye = dyeOptional.map(ClickableItem::getItem).orElse(null);
			if (CustomModelData.of(_dye) == dyeModelData)
				this.dyeType = DyeType.DYE;
			else if (CustomModelData.of(_dye) == stainModelData) {
				this.dyeType = DyeType.STAIN;
			}

			Optional<ClickableItem> resultOptional = contents.get(slot_result);
			ItemStack _result = resultOptional.map(ClickableItem::getItem).orElse(null);

			open(player, cheatMode, dyeType, _dyeable, _dye, _result, dyeChoice, color);
		}

		private void clearChoices(InventoryContents contents) {
			for (int i = 1; i < 7; i++) {
				contents.set(5, i, ClickableItem.NONE);
			}
		}

		private void fillChoices(DyeChoice dyeChoice, InventoryContents contents) {
			this.dyeChoice = dyeChoice;
			int col = 1;
			List<ColoredButton> choices = dyeChoice.getChoices();
			for (int i = 0; i < 7; i++) {
				if (i > choices.size() - 1)
					break;

				ColoredButton button = choices.get(i);
				contents.set(5, col, ClickableItem.from(button.getItem(dyeType, "Select Shade"), e -> setResultItem(button.getColor(), contents)));
				++col;
			}
		}

		private void setResultItem(Color color, InventoryContents contents) {
			if (color == null)
				return;

			boolean validDye = false;
			boolean validDyeable = false;

			Optional<ClickableItem> dyeOptional = contents.get(slot_dye);
			if (dyeOptional.isPresent()) {
				validDye = isValidDyeType(contents);
			}

			Optional<ClickableItem> itemOptional = contents.get(slot_dyeable);
			ItemStack dyeable = null;
			if (itemOptional.isPresent()) {
				dyeable = itemOptional.get().getItem();
				validDyeable = isValidDyeable(contents);
			}

			if (!validDye || !validDyeable)
				return;

			ItemStack _result = dyeable.clone();
			setColor(_result, color);

			this.color = color;
			this.result = _result;
			contents.set(slot_result, ClickableItem.from(_result, e -> confirm(contents)));
		}

		private void confirm(InventoryContents contents) {
			Optional<ClickableItem> dyeableOptional = contents.get(slot_dyeable);
			Optional<ClickableItem> dyeOptional = contents.get(slot_dye);
			Optional<ClickableItem> resultOptional = contents.get(slot_result);

			if (dyeableOptional.isEmpty() || dyeOptional.isEmpty() || resultOptional.isEmpty())
				return;

			if (!isValidDyeable(contents))
				return;

			if (!isValidDyeType(contents))
				return;

			List<ItemStack> returnItems = new ArrayList<>();
			ItemStack _dyeable = dyeableOptional.get().getItem().subtract();
			returnItems.add(_dyeable);

			ItemStack _result = resultOptional.get().getItem();
			returnItems.add(_result);

			ItemStack _dye = dyeOptional.get().getItem();
			if (!cheatMode) {
				if (_dye.getAmount() > 1) {
					ItemStack _dyeExtra = _dye.clone();
					_dyeExtra.subtract();
					_dye.setAmount(1);

					returnItems.add(_dyeExtra);
				}

				_dye = handleDye(_dye).build();
				returnItems.add(_dye);
			}

			PlayerUtils.giveItems(player, returnItems);

			new SoundBuilder(Sound.ITEM_BOTTLE_EMPTY).location(player).pitch(RandomUtils.randomDouble(0.8, 1.2)).play();
			Tasks.wait(8, () -> new SoundBuilder(Sound.ITEM_BOTTLE_FILL).location(player).pitch(RandomUtils.randomDouble(0.8, 1.2)).play());

			getInv(player, cheatMode, dyeType, _dyeable, _dye, _result, dyeChoice, color).close(player);
		}

		private ItemBuilder handleDye(ItemStack dye) {
			ItemBuilder builder = new ItemBuilder(dye);

			List<String> lore = dye.getItemMeta().getLore();
			if (lore == null || lore.isEmpty())
				return builder;

			String _usesLore = StringUtils.stripColor(usesLore);
			List<String> newLore = new ArrayList<>();
			for (String line : lore) {
				String _line = StringUtils.stripColor(line);
				if (_line.contains(_usesLore)) {
					int uses = Integer.parseInt(_line.replaceAll("Uses: ", ""));
					--uses;

					if (uses == 0)
						builder = new ItemBuilder(Material.GLASS_BOTTLE);

					newLore.add(usesLore + uses);
				} else {
					newLore.add(line);
				}
			}

			if (!builder.material().equals(Material.GLASS_BOTTLE))
				builder.setLore(newLore);

			return builder;
		}

		private static boolean isValidDyeType(InventoryContents contents) {
			Optional<ClickableItem> dyeOptional = contents.get(slot_dye);
			if (dyeOptional.isEmpty())
				return false;

			ItemStack dye = dyeOptional.get().getItem();
			if (ItemUtils.isNullOrAir(dye))
				return false;

			if (!Material.PAPER.equals(dye.getType()))
				return false;

			if (!CustomModel.exists(dye))
				return false;

			return ItemUtils.hasLore(dye, usageLore);
		}

		private static boolean isValidDyeable(InventoryContents contents) {
			Optional<ClickableItem> dyeableOptional = contents.get(slot_dyeable);
			if (dyeableOptional.isEmpty())
				return false;

			ItemStack dyeable = dyeableOptional.get().getItem();
			if (ItemUtils.isNullOrAir(dyeable))
				return false;

			return dyeable.getType().equals(Material.LEATHER_HORSE_ARMOR) && CustomModel.exists(dyeable);
		}

		private static void setColor(ItemStack dyeable, Color color) {
			if (dyeable.getItemMeta() instanceof LeatherArmorMeta armorMeta) {
				armorMeta.setColor(color);
				dyeable.setItemMeta(armorMeta);
			}
		}

		private enum StainChoice {
			BIRCH("#FEE496"),
			OAK("#F4C57A"),
			JUNGLE("#EFA777"),
			SPRUCE("#AD7B49"),
			DARK_OAK("#664421"),
			CRIMSON("#924967"),
			ACACIA("#F18648"),
			WARPED("#2FA195"),
			;

			@Getter
			ColoredButton button;

			public ItemStack getItem(String name) {
				return getButton().getItem(DyeType.STAIN, name);
			}

			StainChoice(String hex) {
				this.button = new ColoredButton(hex);
			}
		}

		private enum DyeChoice {
			RED("#FF0000", List.of("#FF756B", "#FF5E52", "#FF4233", "#FF0000", "#C70F00", "#9C0B00", "#6E0800")),
			ORANGE("#FF8000", List.of("#FFFFFF", "#FFFFFF", "#FFFFFF", "#FF8000", "#FFFFFF", "#FFFFFF", "#FFFFFF")),
			YELLOW("#FFFF00", List.of("#FFFFFF", "#FFFFFF", "#FFFFFF", "#FFFF00", "#FFFFFF", "#FFFFFF", "#FFFFFF")),
			PINK("#FF88AA", List.of("#FFFFFF", "#FFFFFF", "#FFFFFF", "#FF88AA", "#FFFFFF", "#FFFFFF", "#FFFFFF")),
			WHITE("#FFFFFF", List.of("#FFFFFF", "#C7C7C7", "#8F8F8F", "#6E6E6E", "#525252", "#333333", "#222222")),
			GREEN("#00FF00", List.of("#FFFFFF", "#FFFFFF", "#FFFFFF", "#00FF00", "#FFFFFF", "#FFFFFF", "#FFFFFF")),
			PURPLE("#8800AA", List.of("#FFFFFF", "#FFFFFF", "#FFFFFF", "#8800AA", "#FFFFFF", "#FFFFFF", "#FFFFFF")),
			BLUE("#0000FF", List.of("#FFFFFF", "#FFFFFF", "#FFFFFF", "#0000FF", "#FFFFFF", "#FFFFFF", "#FFFFFF")),
			LIGHT_BLUE("#00BCFF", List.of("#FFFFFF", "#FFFFFF", "#FFFFFF", "#00BCFF", "#FFFFFF", "#FFFFFF", "#FFFFFF")),
			;

			@Getter
			ColoredButton button;
			@Getter
			List<ColoredButton> choices = new ArrayList<>();

			public ItemStack getItem(String name) {
				return getButton().getItem(DyeType.DYE, name);
			}

			DyeChoice(String hex, List<String> hexes) {
				this.button = new ColoredButton(hex);
				for (String _hex : hexes) {
					choices.add(new ColoredButton(_hex));
				}
			}
		}

		private static class ColoredButton {
			ItemBuilder dye = new ItemBuilder(Material.LEATHER_HORSE_ARMOR).customModelData(1);
			ItemBuilder stain = new ItemBuilder(Material.LEATHER_HORSE_ARMOR).customModelData(2);
			@Getter
			org.bukkit.Color color;

			public ItemStack getItem(@NonNull DyeType dyeType, String name) {
				ItemBuilder dyeable = new ItemBuilder(dye);
				if (dyeType.equals(DyeType.STAIN))
					dyeable = new ItemBuilder(stain);

				if (name != null)
					dyeable.name(name);

				ItemStack result = dyeable.build();
				setColor(result, color);
				return result;
			}

			public ColoredButton(String hex) {
				this.color = ColorType.hexToBukkit(hex);
			}
		}
	}
}
