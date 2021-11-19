package gg.projecteden.nexus.features.custombenches;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.ItemClickData;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

	public static ItemStack rainbowDye = new ItemBuilder(Material.RED_DYE).customModelData(1).name("Rainbow Dye").build();

	@Override
	CustomBenchType getBenchType() {
		return CustomBenchType.DYE_STATION;
	}

	public static void open(Player player) {
		new DyeStationMenu().open(player);
	}

	@NoArgsConstructor
	@AllArgsConstructor
	private static class DyeStationMenu extends MenuUtils implements InventoryProvider, Listener {
		Player player;
		ItemStack dyeable;
		ItemStack dye;
		ItemStack result;
		ColorChoice colorChoice;
		Color color;

		private static final SlotPos slot_dyeable = new SlotPos(1, 1);
		private static final SlotPos slot_dye = new SlotPos(3, 1);
		private static final SlotPos slot_result = new SlotPos(2, 7);

		public void open(Player player) {
			open(player, null, null, null, null, null);
		}

		public void open(Player player, ItemStack dyeable, ItemStack dye, ItemStack result, ColorChoice colorChoice, Color color) {
			this.player = player;
			if (dyeable == null) this.dyeable = new ItemStack(Material.AIR);
			if (dye == null) this.dye = new ItemStack(Material.AIR);
			if (result == null) this.result = new ItemStack(Material.AIR);
			this.colorChoice = colorChoice;
			this.color = color;

			getInv(player, dyeable, dye, result, colorChoice, color).open(player);
		}

		private SmartInventory getInv(Player player, ItemStack dyeable, ItemStack dye, ItemStack result, ColorChoice colorChoice, Color color) {
			return SmartInventory.builder()
				.title(StringUtils.colorize("&fꈉ섈"))
				.size(6, 9)
				.provider(new DyeStationMenu(player, dyeable, dye, result, colorChoice, color))
				.closeable(false)
				.build();
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			contents.set(0, ClickableItem.from(closeItem(), e -> {
				contents.get(slot_dyeable).ifPresent(clickableItem -> PlayerUtils.giveItem(player, clickableItem.getItem()));
				contents.get(slot_dye).ifPresent(clickableItem -> PlayerUtils.giveItem(player, clickableItem.getItem()));

				getInv(player, dyeable, dye, result, colorChoice, color).close(player);
			}));

			contents.set(slot_dyeable, ClickableItem.from(dyeable, e -> replaceItem(player, contents, e, slot_dyeable)));
			contents.set(slot_dye, ClickableItem.from(dye, e -> replaceItem(player, contents, e, slot_dye)));
			if (color != null)
				setResultItem(color, contents);

			int row = 1;
			int col = 3;
			int count = 0;
			for (ColorChoice _colorChoice : ColorChoice.values()) {
				String itemName = StringUtils.camelCase(_colorChoice) + "s";
				contents.set(row, col++, ClickableItem.from(_colorChoice.getItem(itemName), e -> fillChoices(_colorChoice, contents)));
				if (++count == 3) {
					++row;
					col = 3;
					count = 0;
				}
			}

			if (colorChoice != null)
				fillChoices(colorChoice, contents);
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

			Optional<ClickableItem> resultOptional = contents.get(slot_result);
			ItemStack _result = resultOptional.map(ClickableItem::getItem).orElse(null);

			open(player, _dyeable, _dye, _result, colorChoice, color);
		}

		private void fillChoices(ColorChoice colorChoice, InventoryContents contents) {
			this.colorChoice = colorChoice;
			int col = 1;
			List<ColoredButton> choices = colorChoice.getChoices();
			for (int i = 0; i < 7; i++) {
				if (i > choices.size() - 1)
					break;

				ColoredButton button = choices.get(i);
				contents.set(5, col, ClickableItem.from(button.getItem("Select Shade"), e -> setResultItem(button.getColor(), contents)));
				col++;
			}
		}

		private void setResultItem(Color color, InventoryContents contents) {
			if (color == null)
				return;

			boolean validDye = false;
			boolean validDyeable = false;

			Optional<ClickableItem> dyeOptional = contents.get(slot_dye);
			if (dyeOptional.isPresent()) {
				validDye = isValidDye(contents);
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

			if (!isValidDye(contents) || !isValidDyeable(contents))
				return;

			List<ItemStack> returnItems = new ArrayList<>();
			ItemStack _dyeable = dyeableOptional.get().getItem().subtract();
			returnItems.add(_dyeable);

			ItemStack _result = resultOptional.get().getItem();
			returnItems.add(_result);

			ItemStack _dye = dyeOptional.get().getItem();
			if (_dye.getAmount() > 1) {
				ItemStack _dyeExtra = _dye.clone();
				_dyeExtra.subtract();
				_dye.setAmount(1);

				returnItems.add(_dyeExtra);
			}

			ItemBuilder _dyeBuilder = new ItemBuilder(_dye);
			int modelData = _dyeBuilder.customModelData();
			if (modelData < 4) {
				_dyeBuilder.customModelData(++modelData);
			} else
				_dyeBuilder = new ItemBuilder(Material.GLASS_BOTTLE);
			returnItems.add(_dyeBuilder.build());

			PlayerUtils.giveItems(player, returnItems);

			new SoundBuilder(Sound.ITEM_BOTTLE_EMPTY).location(player).pitch(RandomUtils.randomDouble(0.8, 1.2)).play();
			Tasks.wait(8, () -> new SoundBuilder(Sound.ITEM_BOTTLE_FILL).location(player).pitch(RandomUtils.randomDouble(0.8, 1.2)).play());

			getInv(player, _dyeable, _dyeBuilder.build(), _result, colorChoice, color).close(player);
		}

		private static boolean isValidDye(InventoryContents contents) {
			Optional<ClickableItem> dyeOptional = contents.get(slot_dye);
			if (dyeOptional.isEmpty())
				return false;

			ItemStack dye = dyeOptional.get().getItem();
			if (ItemUtils.isNullOrAir(dye))
				return false;

			if (!Material.RED_DYE.equals(dye.getType()))
				return false;

			return CustomModel.exists(dye);
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

		private enum ColorChoice {
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
				return getButton().getItem(name);
			}

			ColorChoice(String hex, List<String> hexes) {
				this.button = new ColoredButton(hex);
				for (String _hex : hexes) {
					choices.add(new ColoredButton(_hex));
				}
			}
		}

		private static class ColoredButton {
			ItemBuilder dyeable = new ItemBuilder(Material.LEATHER_HORSE_ARMOR).customModelData(1);
			@Getter
			org.bukkit.Color color;

			public ItemStack getItem(String name) {
				if (name != null)
					dyeable.name(name);

				ItemStack result = dyeable.build();
				setColor(result, color);
				return result;
			}

			public ColoredButton(String hex) {
				java.awt.Color decode = java.awt.Color.decode(hex);
				this.color = org.bukkit.Color.fromRGB(decode.getRed(), decode.getGreen(), decode.getBlue());
			}
		}
	}
}
