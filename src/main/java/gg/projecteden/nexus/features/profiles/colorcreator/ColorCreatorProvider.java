package gg.projecteden.nexus.features.profiles.colorcreator;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SignMenuFactory;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Rows(5)
@Title("Color Creator")
public class ColorCreatorProvider extends InventoryProvider {
	private static final String PREFIX = StringUtils.getPrefix("ColorCreator");
	private InventoryProvider previousMenu = null;
	private Color displayColor;
	private Consumer<Color> applyColor;
	private Set<CreatedColor> createdColors;
	private Consumer<CreatedColor> renameColor;
	private Consumer<Color> saveColor;
	private Consumer<Color> deleteColor;

	public ColorCreatorProvider(Player viewer, @Nullable InventoryProvider previousMenu, ChatColor defaultColor, Consumer<Color> applyColor, Set<CreatedColor> createdColors, Consumer<CreatedColor> renameColor, Consumer<Color> saveColor, Consumer<Color> deleteColor) {
		this(viewer, previousMenu, defaultColor.getColor(), applyColor, createdColors, renameColor, saveColor, deleteColor);
	}

	public ColorCreatorProvider(Player viewer, @Nullable InventoryProvider previousMenu, java.awt.Color defaultColor, Consumer<Color> applyColor, Set<CreatedColor> createdColors, Consumer<CreatedColor> renameColor, Consumer<Color> saveColor, Consumer<Color> deleteColor) {
		this(viewer, previousMenu, ColorType.toBukkit(defaultColor), applyColor, createdColors, renameColor, saveColor, deleteColor);
	}

	public ColorCreatorProvider(Player viewer, @Nullable InventoryProvider previousMenu, Color defaultColor, Consumer<Color> applyColor, Set<CreatedColor> createdColors, Consumer<CreatedColor> renameColor, Consumer<Color> saveColor, Consumer<Color> deleteColor) {
		this.viewer = viewer;
		this.previousMenu = previousMenu;
		this.displayColor = defaultColor;
		this.applyColor = applyColor;
		this.createdColors = createdColors;
		this.renameColor = renameColor;
		this.saveColor = saveColor;
		this.deleteColor = deleteColor;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class CreatedColor {
		private Color color;
		private String name = "Color";

		public CreatedColor(@NotNull Color color) {
			this.color = color;
		}

		public ChatColor getChatColor() {
			return ChatColor.of(ColorType.toJava(this.color));
		}

		public @Nullable String getColoredName() {
			return getChatColor() + this.name;
		}

		public boolean matches(Color color) {
			return this.color.equals(color);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CreatedColor createdColor))
				return false;

			return this.matches(createdColor.getColor());
		}
	}

	private CreatedColor getSavedColor(@NotNull Color color) {
		for (CreatedColor createdColor : createdColors) {
			if (createdColor.matches(color)) {
				return createdColor;
			}
		}
		return null;
	}

	@Override
	public void init() {
		addBackItem(previousMenu);

		String displayTitle = "&fColor: ";
		CreatedColor createdColor = getSavedColor(displayColor);
		boolean isColorSaved = createdColor != null;
		if (isColorSaved && !createdColor.getName().equalsIgnoreCase("Color"))
			displayTitle += createdColor.getColoredName();

		ItemBuilder displayItem = new ItemBuilder(ItemModelType.DYE_STATION_BUTTON_DYE)
			.name(displayTitle)
			.itemFlags(ItemFlags.HIDE_ALL)
			.dyeColor(displayColor)
			.lore(List.of(
				"&cR: " + displayColor.getRed(),
				"&aG: " + displayColor.getGreen(),
				"&bB: " + displayColor.getBlue(),
				"",
				"&3Hex: &e" + StringUtils.toHex(displayColor),
				"&3- - -",
				"&3&l[&eClick to Apply&3&l]"
			));
		contents.set(SlotPos.of(2, 4), ClickableItem.of(displayItem, e -> {
			applyColor.accept(displayColor);
		}));

		// Save Color
		if (!isColorSaved) {
			ItemBuilder saveColorButton = new ItemBuilder(ItemModelType.GUI_PLUS).dyeColor(ColorType.LIGHT_GREEN)
				.name("&aSave Color?").itemFlags(ItemFlags.HIDE_ALL);

			contents.set(SlotPos.of(3, 4), ClickableItem.of(saveColorButton, e -> {
				saveColor.accept(displayColor);
				refresh();
			}));
		}

		// Saved Colors
		if (!createdColors.isEmpty()) {
			ItemBuilder savedColorsMenu = new ItemBuilder(Material.CHEST).name("&3Saved Colors");
			contents.set(SlotPos.of(3, 1), ClickableItem.of(savedColorsMenu, e -> {
				Consumer<Color> applyColor = _color -> {
					this.displayColor = _color;
					refresh();
				};

				new SavedColorsMenu(viewer, this, createdColors, renameColor, applyColor, deleteColor).open(viewer);
			}));
		}


		// Dyes
		ItemBuilder dyeMenu = new ItemBuilder(Material.RED_DYE).name("&3Pick A Color");
		contents.set(SlotPos.of(1, 1), ClickableItem.of(dyeMenu, e -> {
			new PickColorMenu(viewer, this, _color -> {
				this.displayColor = _color.getColor();
				refresh();
			}).open(viewer);
		}));

		// Hex
		ItemBuilder inputHexMenu = new ItemBuilder(Material.NAME_TAG).name("&3Input Hex");
		contents.set(SlotPos.of(2, 1), ClickableItem.of(inputHexMenu, e -> {
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

	@Title("Saved Colors")
	private static class SavedColorsMenu extends InventoryProvider {
		Set<CreatedColor> choices = new HashSet<>();

		InventoryProvider previousMenu = null;
		Consumer<CreatedColor> renameColor;
		Consumer<Color> applyColor;
		Consumer<Color> unSaveColor;


		public SavedColorsMenu(Player viewer, @Nullable InventoryProvider previousMenu, Set<CreatedColor> choices, Consumer<CreatedColor> renameColor, Consumer<Color> applyColor, Consumer<Color> unSaveColor) {
			this.viewer = viewer;
			this.previousMenu = previousMenu;
			this.choices = choices;
			this.applyColor = applyColor;
			this.renameColor = renameColor;
			this.unSaveColor = unSaveColor;
		}

		@Override
		public void init() {
			addBackItem(previousMenu);

			List<ClickableItem> items = new ArrayList<>();
			for (CreatedColor createdColor : choices) {
				Color color = createdColor.getColor();
				ItemBuilder displayItem = new ItemBuilder(ItemModelType.DYE_STATION_BUTTON_DYE)
					.name(createdColor.getColoredName())
					.itemFlags(ItemFlags.HIDE_ALL).dyeColor(color)
					.lore(List.of(
							"&cR: " + color.getRed(),
							"&aG: " + color.getGreen(),
							"&bB: " + color.getBlue(),
							"",
							"&3Hex: &e" + StringUtils.toHex(color),
							"&3- - -",
						"&eLClick &3to &aapply",
						"&eShift+LClick &3to &cdelete",
						"&eRClick &3to &brename"
						));

				items.add(ClickableItem.of(displayItem, e -> {
					if (e.isRightClick()) {
						Nexus.getSignMenuFactory().lines(List.of("", SignMenuFactory.ARROWS, "Enter a new name", "for the color"))
							.prefix(PREFIX)
							.response(lines -> {
								String name = lines[0];
								if (Censor.isCensored(e.getPlayer(), name)) {
									PlayerUtils.send(viewer, PREFIX + "&cInappropriate input");
								} else {
									createdColor.setName(name);
									renameColor.accept(createdColor);
								}

								refresh();
							})
							.open(viewer);

					} else {
						if (e.isShiftClick()) {
							unSaveColor.accept(createdColor.getColor());
							refresh();
						} else {
							applyColor.accept(createdColor.getColor());
							back(previousMenu);
						}
					}
				}));
			}
			paginate(items);
		}
	}

	@Rows(4)
	@Title("Select A Color")
	private static class PickColorMenu extends InventoryProvider {

		private static final List<ColorType> DYE_COLORS = List.of(ColorType.RED, ColorType.ORANGE, ColorType.YELLOW, ColorType.LIGHT_GREEN,
			ColorType.GREEN, ColorType.CYAN, ColorType.LIGHT_BLUE, ColorType.BLUE, ColorType.PURPLE, ColorType.MAGENTA, ColorType.PINK,
			ColorType.BROWN, ColorType.BLACK, ColorType.GRAY, ColorType.LIGHT_GRAY, ColorType.WHITE);

		InventoryProvider previousMenu = null;
		Consumer<CreatedColor> applyColor;

		public PickColorMenu(Player viewer, @Nullable InventoryProvider previousMenu, Consumer<CreatedColor> applyColor) {
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
					applyColor.accept(new CreatedColor(colorType.getBukkitColor()));
					back(previousMenu);
				}));
			}
		}

	}
}
