package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SignMenuFactory;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.interfaces.IsColored;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/*
	TODO:
		- Ability to save colors
 */
@Rows(5)
@Title("Color Creator")
public class ColorCreatorProvider extends InventoryProvider {
	private static final String PREFIX = StringUtils.getPrefix("ColorCreator");
	InventoryProvider previousMenu = null;
	Color displayColor;
	Consumer<Color> applyColor;

	public ColorCreatorProvider(Player viewer, @Nullable InventoryProvider previousMenu, IsColored defaultColor, Consumer<Color> applyColor) {
		this(viewer, previousMenu, defaultColor.colored().getBukkitColor(), applyColor);
	}

	public ColorCreatorProvider(Player viewer, @Nullable InventoryProvider previousMenu, ChatColor defaultColor, Consumer<Color> applyColor) {
		this(viewer, previousMenu, defaultColor.getColor(), applyColor);
	}

	public ColorCreatorProvider(Player viewer, @Nullable InventoryProvider previousMenu, java.awt.Color defaultColor, Consumer<Color> applyColor) {
		this(viewer, previousMenu, ColorType.toBukkit(defaultColor), applyColor);
	}

	public ColorCreatorProvider(Player viewer, @Nullable InventoryProvider previousMenu, Color defaultColor, Consumer<Color> applyColor) {
		this.viewer = viewer;
		this.previousMenu = previousMenu;
		this.displayColor = defaultColor;
		this.applyColor = applyColor;
	}

	@Override
	public void init() {
		addBackItem(previousMenu);

		ItemBuilder displayItem = new ItemBuilder(CustomMaterial.GUI_FILLER_DYEABLE).name("&fCurrent Color:")
			.itemFlags(ItemFlags.HIDE_ALL)
			.dyeColor(displayColor)
			.lore(List.of(
				"&cR: " + displayColor.getRed(),
				"&aG: " + displayColor.getGreen(),
				"&bB: " + displayColor.getBlue(),
				"",
				"&3Hex: &e" + StringUtils.toHex(displayColor),
				"",
				"&3&l[&eClick to Apply&3&l]"
			));
		contents.set(2, 4, ClickableItem.of(displayItem, e -> {
			applyColor.accept(displayColor);
			back(previousMenu);
		}));

		// Dyes
		ItemBuilder dyeMenu = new ItemBuilder(Material.RED_DYE).name("Pick A Color");
		contents.set(SlotPos.of(1, 1), ClickableItem.of(dyeMenu, e -> {
			new PickColorMenu(viewer, this, _color -> {
				this.displayColor = _color;
				refresh();
			}).open(viewer);
		}));

		// Hex
		ItemBuilder inputHex = new ItemBuilder(Material.NAME_TAG).name("Input Hex");
		contents.set(SlotPos.of(3, 1), ClickableItem.of(inputHex, e -> {
			Nexus.getSignMenuFactory().lines(List.of("#", SignMenuFactory.ARROWS, "Enter a", "hex color"))
				.prefix(PREFIX)
				.response(lines -> {
					String hex = lines[0];
					try {
						ChatColor chatColor = ChatColor.of(hex);
						this.displayColor = ColorType.toBukkitColor(chatColor);
					} catch (Exception ignored) {
						PlayerUtils.send(viewer, PREFIX + "&cCouldn't parse hex from " + hex);
					}
					refresh();
				})
				.open(viewer);
		}));

		fillRGB();
	}

	private void fillRGB() {
		int[] slotsArr = new int[]{6, 7, 8};
		int[] amountArr = new int[]{1, 10, 64};

		for (int i = 0; i < RGB.values().length; i++) {
			for (int j = 0; j < 3; j++) {
				RGB rgb = RGB.values()[i];
				int itemAmount = amountArr[j];

				ItemBuilder rgbItem = new ItemBuilder(rgb.getColorType().getDye())
					.amount(itemAmount)
					.name(rgb.getChatColor() + rgb.name() + ": " + "+/- " + amountArr[j])
					.lore(List.of(
						"&cR: " + displayColor.getRed(),
						"&aG: " + displayColor.getGreen(),
						"&bB: " + displayColor.getBlue()
					));

				contents.set(i + 1, slotsArr[j], ClickableItem.of(rgbItem, e -> {
					this.displayColor = rgb.getUpdatedColor(displayColor, e.isLeftClick(), itemAmount);
					refresh();
				}));
			}
		}
	}

	@AllArgsConstructor
	enum RGB {
		R(ColorType.RED, ColorType.LIGHT_RED),
		G(ColorType.LIGHT_GREEN, ColorType.LIGHT_GREEN),
		B(ColorType.BLUE, ColorType.LIGHT_BLUE);

		@Getter
		final ColorType colorType;
		final ColorType chatColorType;

		public ChatColor getChatColor() {
			return chatColorType.getChatColor();
		}

		public @NonNull Color getUpdatedColor(Color color, boolean leftClick, int itemAmount) {
			Color newColor = color;
			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();

			switch (this) {
				case R -> {
					if (leftClick)
						newColor = color.setRed(Math.min(red + itemAmount, 255));
					else
						newColor = color.setRed(Math.max(red - itemAmount, 0));
				}
				case G -> {
					if (leftClick)
						newColor = color.setGreen(Math.min(green + itemAmount, 255));
					else
						newColor = color.setGreen(Math.max(green - itemAmount, 0));
				}
				case B -> {
					if (leftClick)
						newColor = color.setBlue(Math.min(blue + itemAmount, 255));
					else
						newColor = color.setBlue(Math.max(blue - itemAmount, 0));
				}
			}

			return newColor;
		}
	}

	@Rows(4)
	@Title("Select A Color")
	private static class PickColorMenu extends InventoryProvider {

		private static final List<ColorType> DYE_COLORS = List.of(ColorType.RED, ColorType.ORANGE, ColorType.YELLOW, ColorType.LIGHT_GREEN,
			ColorType.GREEN, ColorType.CYAN, ColorType.LIGHT_BLUE, ColorType.BLUE, ColorType.PURPLE, ColorType.MAGENTA, ColorType.PINK,
			ColorType.BROWN, ColorType.BLACK, ColorType.GRAY, ColorType.LIGHT_GRAY, ColorType.WHITE);

		InventoryProvider previousMenu = null;
		Consumer<Color> applyColor;

		public PickColorMenu(Player viewer, @Nullable InventoryProvider previousMenu, Consumer<Color> applyColor) {
			this.viewer = viewer;
			this.previousMenu = previousMenu;
			this.applyColor = applyColor;
		}

		@Override
		public void init() {
			addBackItem(previousMenu);

			int row = 1;
			int col = 0;
			for (ColorType colorType : DYE_COLORS) {
				Color color = colorType.getBukkitColor();
				if (colorType == ColorType.BLACK)
					color = Color.fromRGB(51, 51, 51); // easier to read in *all cases*

				ItemBuilder colorItem = new ItemBuilder(colorType.getDye())
					.name(colorType.getChatColor() + StringUtils.camelCase(colorType))
					.lore(List.of(
						"&cR: " + color.getRed(),
						"&aG: " + color.getGreen(),
						"&bB: " + color.getBlue(),
						"",
						"&3Hex: &e" + StringUtils.toHex(color))
					);

				if (col > 8) {
					++row;
					col = 0;
				}

				contents.set(new SlotPos(row, col++), ClickableItem.of(colorItem, e -> {
					applyColor.accept(colorType.getBukkitColor());
					back(previousMenu);
				}));
			}
		}

	}
}
