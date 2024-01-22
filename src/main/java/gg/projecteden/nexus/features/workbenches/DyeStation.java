package gg.projecteden.nexus.features.workbenches;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu.DyeChoice;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Unreleased;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import lombok.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shapeless;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

// TODO: WHEN CLICK NEXT PAGE ON WOOD TYPES, IT GIVES THE ORIGINAL ITEM TO THE PLAYER AND ALSO CLONES IT INTO THE NEXT MENU, DUPLICATING THE ITEM
@Unreleased
@NoArgsConstructor
public class DyeStation extends CustomBench implements ICraftableCustomBench {
	private static final String USAGE_LORE = "&3Used in Dye Station";
	private static final int MAX_USES = 5;
	public static final int MAX_USES_PAINTBRUSH = MAX_USES * 2;
	public static final String USES_LORE = "&3Uses: &e";

	private static final ItemBuilder WORKBENCH = new ItemBuilder(CustomMaterial.DYE_STATION).name("Dye Station");

	public static ItemBuilder getWorkbench() {
		return WORKBENCH.clone();
	}

	private static final ItemBuilder MAGIC_DYE = new ItemBuilder(DyeType.DYE.getBottleMaterial())
		.name(Gradient.of(List.of(ChatColor.RED, ChatColor.YELLOW, ChatColor.AQUA)).apply("Magic Dye"))
		.lore(USAGE_LORE, USES_LORE + MAX_USES);

	public static ItemBuilder getMagicDye() {
		return MAGIC_DYE.clone();
	}

	private static final ItemBuilder MAGIC_STAIN = new ItemBuilder(DyeType.STAIN.getBottleMaterial())
		.name(Gradient.of(List.of(ChatColor.of("#e0a175"), ChatColor.of("#5c371d"))).apply("Magic Stain"))
		.lore(USAGE_LORE, USES_LORE + MAX_USES);

	public static ItemBuilder getMagicStain() {
		return MAGIC_STAIN.clone();
	}

	private static final ItemBuilder PAINTBRUSH = new ItemBuilder(CustomMaterial.PAINTBRUSH)
		.name("&ePaintbrush")
		.lore(USES_LORE + MAX_USES_PAINTBRUSH)
		.dyeColor(ColorType.WHITE);

	public static ItemBuilder getPaintbrush() {
		return PAINTBRUSH.clone();
	}

	@Override
	CustomBenchType getBenchType() {
		return CustomBenchType.DYE_STATION;
	}

	@Override
	public RecipeBuilder<?> getBenchRecipe() {
		return shaped("111", "232", "242")
			.add('1', Material.WHITE_WOOL)
			.add('2', Tag.PLANKS)
			.add('3', getMagicDye().build())
			.add('4', getMagicStain().build())
			.toMake(getWorkbench().build());
	}

	@Override
	public List<RecipeBuilder<?>> getAdditionRecipes() {
		List<RecipeBuilder<?>> recipes = new ArrayList<>();

		// Magic Dye
		recipes.add(
			shapeless(Material.GLASS_BOTTLE, Material.RED_DYE, Material.ORANGE_DYE, Material.YELLOW_DYE,
				Material.GREEN_DYE, Material.CYAN_DYE, Material.BLUE_DYE, Material.PURPLE_DYE, Material.PINK_DYE)
				.toMake(DyeStation.getMagicDye().build())
		);

		// Magic Stain
		recipes.add(
			shapeless(Material.GLASS_BOTTLE, Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS,
				Material.DARK_OAK_PLANKS, Material.OAK_SAPLING, Material.SPRUCE_SAPLING, Material.BIRCH_SAPLING, Material.DARK_OAK_SAPLING)
				.toMake(DyeStation.getMagicStain().build())
		);

		return recipes;
	}

	public static void open(Player player) {
		if (!DecorationUtils.canUseFeature(player, DecorationType.DYE_STATION.getConfig())) {
			PlayerUtils.send(player, DecorationUtils.getPrefix() + "&cYou cannot use this feature yet");
			return;
		}

		DyeStationMode mode = DyeStationMode.NORMAL;
		if (DecorationUtils.hasBypass(player))
			mode = DyeStationMode.CHEAT;

		open(player, mode);
	}

	public static void open(Player player, DyeStationMode mode) {
		new DyeStationMenu(mode).open(player);
	}

	@Getter
	@AllArgsConstructor
	public enum DyeType {
		DYE(CustomMaterial.DYE_STATION_DYE, CustomMaterial.DYE_STATION_BUTTON_DYE),
		STAIN(CustomMaterial.DYE_STATION_STAIN, CustomMaterial.DYE_STATION_BUTTON_STAIN),
		;

		private final CustomMaterial bottleMaterial;
		private final CustomMaterial buttonMaterial;

		public ItemBuilder getItem() {
			return new ItemBuilder(bottleMaterial);
		}

		public ItemBuilder getButton() {
			return new ItemBuilder(buttonMaterial);
		}
	}

	public enum DyeStationMode {
		NORMAL,
		CHEAT,
		COSTUME,
	}

	@NoArgsConstructor
	public static class DyeStationMenu extends InventoryProvider implements Listener {
		private DyeStationMode mode = DyeStationMode.NORMAL;
		private DyeStationData data;
		private int colorPage = 0;

		public DyeStationMenu(DyeStationMode mode) {
			this.mode = mode == null ? DyeStationMode.NORMAL : mode;
			this.colorPage = 0;
		}

		public DyeStationMenu(DyeStationMode mode, DyeStationData data, int colorPage) {
			this.mode = mode == null ? DyeStationMode.NORMAL : mode;
			this.data = data;
			this.colorPage = colorPage;
		}

		private static final SlotPos SLOT_INPUT = new SlotPos(1, 1);
		private static final SlotPos SLOT_COSTUME = new SlotPos(2, 1);
		private static final SlotPos SLOT_DYE = new SlotPos(3, 1);
		private static final SlotPos SLOT_RESULT = new SlotPos(2, 7);
		private static final SlotPos SLOT_CHEAT_DYE = new SlotPos(0, 3);
		private static final SlotPos SLOT_CHEAT_STAIN = new SlotPos(0, 5);

		private static final SlotPos SLOT_STAIN_PREVIOUS = new SlotPos(5, 1);
		private static final SlotPos SLOT_STAIN_NEXT = new SlotPos(5, 7);

		private static final ItemBuilder STAIN_NEXT = new ItemBuilder(CustomMaterial.GUI_ARROW_NEXT)
			.dyeColor(ColorType.CYAN)
			.itemFlags(ItemFlag.HIDE_DYE)
			.name("Next Page");

		private static final ItemBuilder STAIN_PREVIOUS = new ItemBuilder(CustomMaterial.GUI_ARROW_PREVIOUS)
			.dyeColor(ColorType.CYAN)
			.itemFlags(ItemFlag.HIDE_DYE)
			.name("Previous Page");

		@Override
		public void open(Player viewer) {
			if (this.data == null) {
				this.data = DyeStationData.builder()
					.player(viewer)
					.cheatMode(mode != DyeStationMode.NORMAL)
					.showButtons(true)
					.inputSlot(SLOT_INPUT)
					.title("섈")
					.onConfirm(data1 -> PlayerUtils.giveItems(viewer, data1.getReturnItems()))
					.build();
			}

			super.open(data.getPlayer());
		}

		public void openCostume(CostumeUser user, Costume costume, Consumer<DyeStationData> onConfirm) {
			this.mode = DyeStationMode.COSTUME;
			if (this.data == null) {
				this.data = DyeStationData.builder()
					.player(user.getOnlinePlayer())
					.cheatMode(true)
					.input(user.getCostumeDisplayItem(costume))
					.inputSlot(SLOT_COSTUME)
					.title("膛")
					.onConfirm(onConfirm)
					.build();
			}

			super.open(data.getPlayer());
		}

		@Override
		public String getTitle() {
			return "&fꈉ" + data.getTitle();
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			this.contents.get(data.getInputSlot()).ifPresent(clickableItem -> {
				if (!SLOT_COSTUME.equals(data.getInputSlot()))
					PlayerUtils.giveItem(viewer, clickableItem.getItem());
			});
			this.contents.get(SLOT_DYE).ifPresent(clickableItem -> {
				if (!data.isCheatMode())
					PlayerUtils.giveItem(viewer, clickableItem.getItem());
			});
		}

		@Override
		public void init() {
			if (!Rank.of(viewer).isStaff() && mode != DyeStationMode.COSTUME)
				throw new InvalidInputException("Temporarily disabled"); // TODO DECORATIONS - Remove on release

			addCloseItem();

			contents.set(data.getInputSlot(), ClickableItem.of(data.getInput(), e -> replaceItem(viewer, contents, e, data.getInputSlot())));
			if (mode.equals(DyeStationMode.NORMAL))
				contents.set(SLOT_DYE, ClickableItem.of(data.getDye(), e -> replaceItem(viewer, contents, e, SLOT_DYE)));
			else
				contents.set(SLOT_DYE, ClickableItem.empty(data.getDye()));

			if (data.getColor() != null)
				setResultItem(data.getColor());

			emptyColors();

			if (mode.equals(DyeStationMode.NORMAL) && !isValidDyeType(contents))
				return;

			fillColors(data.getDyeType(), colorPage);

			if (data.getDyeType().equals(DyeType.DYE) && data.getDyeChoice() != null)
				fillChoices(data.getDyeChoice());

			if (data.isCheatMode() && data.isShowButtons()) {
				contents.set(SLOT_CHEAT_DYE, ClickableItem.of(getMagicDye().resetLore().build(),
					e -> setCheatDyeItem(contents, getMagicDye().build())));
				contents.set(SLOT_CHEAT_STAIN, ClickableItem.of(getMagicStain().resetLore().build(),
					e -> setCheatDyeItem(contents, getMagicStain().build())));
			}
		}

		private void emptyColors() {
			// main colors
			for (int row = 1; row < 4; row++) {
				for (int col = 3; col < 6; col++) {
					contents.set(SlotPos.of(row, col), ClickableItem.AIR);
				}
			}

			// color choices
			for (int i = 1; i < 8; i++) {
				contents.set(SlotPos.of(5, i), ClickableItem.AIR);
			}
		}

		private void setCheatDyeItem(InventoryContents contents, ItemStack item) {
			contents.set(SLOT_DYE, ClickableItem.empty(item));
			reopenMenu(contents);
		}

		private void fillColors(DyeType dyeType, int colorPage) {
			int row = 1;
			int col = 3;
			int index = 0;
			int countAdded = 0;


			switch (dyeType) {
				case DYE -> {
					for (DyeChoice dyeChoice : DyeChoice.values()) {
						String itemName = StringUtils.camelCase(dyeChoice) + "s";
						contents.set(row, col++, ClickableItem.of(dyeChoice.getItem(itemName), e -> fillChoices(dyeChoice)));

						if (++index == 3) {
							++row;
							col = 3;
							index = 0;
						}
					}
				}

				case STAIN -> {
					int skipCount = colorPage * 9;

					for (StainChoice stainChoice : StainChoice.values()) {
						if (skipCount > index) {
							index++;
							continue;
						}

						if (countAdded >= 9) {
							continue;
						}

						String itemName = StringUtils.camelCase(stainChoice);
						Color color = stainChoice.getButton().getColor();
						contents.set(row, col++, ClickableItem.of(stainChoice.getItem(itemName), e -> setResultItem(color)));
						countAdded++;

						if (++index == 3) {
							++row;
							col = 3;
							index = 0;
						}
					}

					if (Math.ceil(StainChoice.values().length / 9.0) > (colorPage + 1))
						contents.set(SLOT_STAIN_NEXT, ClickableItem.of(STAIN_NEXT, e -> new DyeStationMenu(mode, data, colorPage + 1).open(viewer)));

					if (colorPage > 0)
						contents.set(SLOT_STAIN_PREVIOUS, ClickableItem.of(STAIN_PREVIOUS, e -> new DyeStationMenu(mode, data, colorPage - 1).open(viewer)));
				}
			}
		}

		private void replaceItem(Player player, InventoryContents contents, ItemClickData e, SlotPos slot) {
			if (data.getInputSlot() == SLOT_COSTUME)
				return;

			ItemStack cursorItem = player.getItemOnCursor();
			boolean emptyCursor = isNullOrAir(cursorItem);
			ItemStack slotItem = e.getItem();

			if (isNullOrAir(slotItem) && emptyCursor)
				return;

			// if slot is empty
			if (isNullOrAir(slotItem)) {
				contents.set(slot, ClickableItem.empty(cursorItem));
				player.setItemOnCursor(null);

				contents.set(SLOT_RESULT, ClickableItem.empty(data.getResult()));
				reopenMenu(contents);

				// if slot is not empty
			} else {
				if (emptyCursor) {
					contents.set(slot, ClickableItem.empty(new ItemStack(Material.AIR)));
					contents.set(SLOT_RESULT, ClickableItem.empty(new ItemStack(Material.AIR)));
				} else {
					contents.set(slot, ClickableItem.empty(cursorItem));
					player.setItemOnCursor(null);
				}

				reopenMenu(contents);
				player.setItemOnCursor(slotItem);
			}
		}

		private void reopenMenu(InventoryContents contents) {
			Optional<ClickableItem> itemOptional = contents.get(data.getInputSlot());
			ItemStack input = itemOptional.map(ClickableItem::getItem).orElse(null);

			Optional<ClickableItem> dyeOptional = contents.get(SLOT_DYE);
			ItemStack dye = dyeOptional.map(ClickableItem::getItem).orElse(null);
			if (CustomMaterial.of(dye) == DyeType.DYE.getBottleMaterial())
				data.setDyeType(DyeType.DYE);
			else if (CustomMaterial.of(dye) == DyeType.STAIN.getBottleMaterial())
				data.setDyeType(DyeType.STAIN);

			Optional<ClickableItem> resultOptional = contents.get(SLOT_RESULT);
			ItemStack result = resultOptional.map(ClickableItem::getItem).orElse(null);

			data.setInput(input);
			data.setDye(dye);
			data.setResult(result);

			init();
		}

		private void fillChoices(DyeChoice dyeChoice) {
			data.setDyeChoice(dyeChoice);
			int col = 1;
			List<ColoredButton> choices = dyeChoice.getChoices();
			for (int i = 0; i < 7; i++) {
				if (i > choices.size() - 1)
					break;

				ColoredButton button = choices.get(i);
				contents.set(5, col, ClickableItem.of(button.getItem(data.getDyeType(), "Select Shade"),
					e -> setResultItem(button.getColor())));
				++col;
			}
		}

		private void setResultItem(Color color) {
			if (color == null)
				return;

			boolean validInput = isValidInput(contents);
			boolean validDye = isValidDyeType(contents);

			if (!validDye || !validInput)
				return;

			ItemStack item = new ItemBuilder(contents.get(data.getInputSlot()).orElseThrow().getItem()).dyeColor(color).build();

			final ItemStack result = DecorationUtils.updateLore(item, viewer);

			data.setColor(color);
			data.setResult(result);

			contents.set(SLOT_RESULT, ClickableItem.of(result, e -> confirm()));
		}

		private void confirm() {
			Optional<ClickableItem> inputOptional = contents.get(data.getInputSlot());
			Optional<ClickableItem> dyeOptional = contents.get(SLOT_DYE);
			Optional<ClickableItem> resultOptional = contents.get(SLOT_RESULT);

			if (inputOptional.isEmpty() || dyeOptional.isEmpty() || resultOptional.isEmpty())
				return;

			if (!isValidInput(contents))
				return;

			if (!isValidDyeType(contents))
				return;

			List<ItemStack> returnItems = new ArrayList<>();
			ItemStack input = inputOptional.get().getItem().subtract();
			returnItems.add(input);

			Player player = data.getPlayer();
			ItemStack result = resultOptional.get().getItem();
			returnItems.add(result);

			ItemStack dye = dyeOptional.get().getItem();
			if (!data.isCheatMode()) {
				if (dye.getAmount() > 1) {
					ItemStack dyeExtra = dye.clone();
					dyeExtra.subtract();
					dye.setAmount(1);

					DecorationUtils.debug(viewer, "Adding extra dye items");
					returnItems.add(dyeExtra);
				}

				DecorationUtils.debug(viewer, "Adding original dye");
				returnItems.add(handleDye(dye).build());

				new SoundBuilder(Sound.ITEM_BOTTLE_EMPTY).location(player).pitch(RandomUtils.randomDouble(0.8, 1.2)).play();
				Tasks.wait(8, () -> new SoundBuilder(Sound.ITEM_BOTTLE_FILL).location(player).pitch(RandomUtils.randomDouble(0.8, 1.2)).play());
			}

			data.setInput(input);
			data.setDye(dye);
			data.setResult(result);
			data.onConfirm(returnItems);

			player.closeInventory();
		}

		private ItemBuilder handleDye(ItemStack dye) {
			ItemBuilder builder = new ItemBuilder(dye.clone());

			List<String> lore = dye.getItemMeta().getLore();
			if (lore == null || lore.isEmpty())
				return builder;

			builder = decreaseUses(builder);
			dye.setType(Material.AIR);

			return builder;
		}

		private boolean isValidDyeType(InventoryContents contents) {
			if (data.isCheatMode())
				return true;

			Optional<ClickableItem> dyeOptional = contents.get(SLOT_DYE);
			if (dyeOptional.isEmpty())
				return false;

			ItemStack dye = dyeOptional.get().getItem();
			if (isNullOrAir(dye))
				return false;

			if (!Material.PAPER.equals(dye.getType()))
				return false;

			if (!CustomModel.exists(dye))
				return false;

			return ItemUtils.hasLore(dye, USAGE_LORE);
		}

		private boolean isValidInput(InventoryContents contents) {
			Optional<ClickableItem> inputOptional = contents.get(data.getInputSlot());
			if (inputOptional.isEmpty())
				return false;

			ItemStack input = inputOptional.get().getItem();
			if (isNullOrAir(input))
				return false;

			return MaterialTag.DYEABLE.isTagged(input);
		}

		public interface ColorChoice {
			void apply(ItemStack item);
		}

		@Getter
		public enum StainChoice implements ColorChoice {
			BIRCH("#FEE496"),
			OAK("#F4C57A"),
			JUNGLE("#EFA777"),
			SPRUCE("#AD7B49"),
			DARK_OAK("#664421"),
			CRIMSON("#924967"),
			ACACIA("#F18648"),
			WARPED("#2FA195"),
			MANGROVE("#7F3535"),
			CHERRY("#FFBBBB"),
			BAMBOO("#F3DF5F"),
			;

			private final ColoredButton button;

			public ItemStack getItem(String name) {
				return getButton().getItem(DyeType.STAIN, name);
			}

			public Color getColor() {
				return getButton().getColor();
			}

			StainChoice(String hex) {
				this.button = new ColoredButton(hex);
			}

			@Override
			public void apply(ItemStack item) {
				Colored.of(this.getColor()).apply(item);
			}
		}

		@Getter
		public enum DyeChoice implements ColorChoice {
			RED("#FF0000", List.of("#FF756B", "#FF5E52", "#FF4233", "#FF0000", "#C70F00", "#9C0B00", "#6E0800")),
			ORANGE("#FF7F00", List.of("#FFBF6B", "#FFB552", "#FFA833", "#FF7F00", "#C77200", "#9C5900", "#6E3F00")),
			YELLOW("#FEFF00", List.of("#F5FF6B", "#F2FF52", "#EFFF33", "#FEFF00", "#B7C700", "#909C00", "#656E00")),
			PINK("#FF54BD", List.of("#FF9CD3", "#FF8BCA", "#FF76C0", "#FF54BD", "#FF2E9F", "#DB3690", "#B33F7E")),
			WHITE("#FFFFFF", List.of("#FFFFFF", "#C7C7C7", "#8F8F8F", "#6E6E6E", "#525252", "#333333", "#222222")),
			GREEN("#7FFF00", List.of("#ABFF6B", "#9CFF52", "#89FF33", "#7FFF00", "#54C700", "#429C00", "#2E6E00")),
			PURPLE("#A900FF", List.of("#D76BFF", "#D152FF", "#CA33FF", "#A900FF", "#9300C7", "#73009C", "#51006E")),
			BLUE("#0040FF", List.of("#6B86FF", "#5271FF", "#3357FF", "#0040FF", "#0023C7", "#001C9C", "#001D73")),
			LIGHT_BLUE("#00BEFF", List.of("#6BD0FF", "#52C7FF", "#33BCFF", "#00BEFF", "#0086C7", "#00699C", "#004A6E")),
			;

			private final ColoredButton button;
			private final List<ColoredButton> choices = new ArrayList<>();

			public ItemStack getItem(String name) {
				return getButton().getItem(DyeType.DYE, name);
			}

			public Color getColor() {
				return getButton().getColor();
			}

			DyeChoice(String hex, List<String> hexes) {
				this.button = new ColoredButton(hex);
				for (String _hex : hexes)
					choices.add(new ColoredButton(_hex));
			}

			@Override
			public void apply(ItemStack item) {
				Colored.of(this.getColor()).apply(item);
			}
		}

		@Getter
		public static class ColoredButton {
			private final org.bukkit.Color color;

			public ColoredButton(String hex) {
				this.color = ColorType.hexToBukkit(hex);
			}

			public ItemStack getItem(@NonNull DyeType dyeType, String name) {
				ItemBuilder dye = dyeType.getButton();

				if (name != null)
					dye.name(name);

				return dye.dyeColor(color).build();
			}
		}
	}

	@Data
	public static class DyeStationData {
		private final Player player;
		private final boolean cheatMode;
		private final boolean showButtons;
		private final SlotPos inputSlot;
		private final String title;
		private final Consumer<DyeStationData> onConfirm;
		private DyeType dyeType;
		private ItemStack input;
		private ItemStack dye;
		private ItemStack result;
		private DyeChoice dyeChoice;
		private Color color;
		private List<ItemStack> returnItems;

		@Builder
		public DyeStationData(Player player, boolean cheatMode, boolean showButtons,
							  SlotPos inputSlot, Consumer<DyeStationData> onConfirm, String title,
							  DyeType dyeType, ItemStack input, ItemStack dye,
							  ItemStack result, DyeChoice dyeChoice, Color color) {
			this.player = player;
			this.cheatMode = cheatMode;
			this.showButtons = showButtons;
			this.inputSlot = inputSlot;
			this.title = title;
			this.onConfirm = onConfirm;

			this.dyeType = dyeType;
			if (dyeType == null)
				this.dyeType = DyeType.DYE;

			this.input = input;
			if (input == null)
				this.input = new ItemStack(Material.AIR);

			this.dye = dye;
			if (dye == null) {
				this.dye = new ItemStack(Material.AIR);
				if (cheatMode && showButtons)
					this.dye = getMagicDye().build();
			}

			this.result = result;
			if (result == null)
				this.result = new ItemStack(Material.AIR);

			this.dyeChoice = dyeChoice;
			this.color = color;
		}

		public void onConfirm(List<ItemStack> items) {
			returnItems = items;
			if (onConfirm != null)
				onConfirm.accept(this);
		}
	}

	public static boolean isMagicPaintbrush(ItemStack item) {
		return getPaintbrush().modelId() == ModelId.of(item);
	}

	public static ItemBuilder decreaseUses(ItemBuilder builder) {
		List<String> newLore = new ArrayList<>();
		boolean isPaintbrush = isMagicPaintbrush(builder.build());
		boolean isEmptyBottle = false;

		for (String line : builder.getLore()) {
			String _line = stripColor(line);
			if (_line.contains(stripColor(USES_LORE))) {
				int uses = Integer.parseInt(_line.replaceAll("Uses: ", ""));
				--uses;

				if (uses == 0) {
					if (!isPaintbrush) {
						isEmptyBottle = true;
						builder = new ItemBuilder(Material.GLASS_BOTTLE);
					}
				}

				newLore.add(USES_LORE + uses);
			} else
				newLore.add(line);
		}

		if (!isEmptyBottle)
			builder.setLore(newLore);

		builder.setLore(newLore);
		return builder;
	}

	public static int getUses(ItemStack itemStack) {
		for (String line : new ItemBuilder(itemStack).getLore()) {
			String _line = stripColor(line);
			if (_line.contains(stripColor(USES_LORE)))
				return Integer.parseInt(_line.replaceAll("Uses: ", ""));
		}

		return -1;
	}
}
