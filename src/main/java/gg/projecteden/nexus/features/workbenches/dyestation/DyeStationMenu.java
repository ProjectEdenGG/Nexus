package gg.projecteden.nexus.features.workbenches.dyestation;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@NoArgsConstructor
public class DyeStationMenu extends InventoryProvider implements Listener {
	private DyeStationMode mode = DyeStationMode.NORMAL;
	private DyeStationData data;
	private int dyePage = 0;

	public enum DyeStationMode {
		NORMAL,
		CHEAT,
		COSTUME,
	}

	public DyeStationMenu(DyeStationMode mode) {
		this.mode = mode == null ? DyeStationMode.NORMAL : mode;
		this.dyePage = 0;
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

	public static ItemBuilder decreaseUses(ItemBuilder builder) {
		List<String> newLore = new ArrayList<>();
		boolean isPaintbrush = DyeStation.isMagicPaintbrush(builder.build());

		boolean handledUses = false;
		for (String line : builder.getLore()) {
			String _line = stripColor(line);

			if (_line.contains(stripColor(DyeStation.USES_LORE))) {
				if (!handledUses) {
					handledUses = true;
					int uses = Integer.parseInt(_line.replaceAll("Uses: ", ""));
					--uses;

					if (uses == 0) {
						if (!isPaintbrush) {
							builder = new ItemBuilder(Material.GLASS_BOTTLE);
							return builder;
						}

						builder = DyeStation.getPaintbrush();
						return builder;
					}

					newLore.add(DyeStation.USES_LORE + uses);
					continue;
				}
			}

			newLore.add(line);
		}

		builder.setLore(newLore);
		return builder;
	}

	public static int getUses(ItemStack itemStack) {
		for (String line : new ItemBuilder(itemStack).getLore()) {
			String _line = stripColor(line);
			if (_line.contains(stripColor(DyeStation.USES_LORE)))
				return Integer.parseInt(_line.replaceAll("Uses: ", ""));
		}

		return -1;
	}

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

		dyePage = data.getDyePage();

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

		fillColors(data.getDyeType(), dyePage);

		if (data.getDyeType().equals(ColorChoice.ChoiceType.DYE) && data.getDyeChoice() != null)
			fillChoices(data.getDyeChoice());

		if (data.isCheatMode() && data.isShowButtons()) {
			contents.set(SLOT_CHEAT_DYE, ClickableItem.of(DyeStation.getMagicDye().resetLore().build(),
					e -> setCheatDyeItem(contents, DyeStation.getMagicDye().build())));
			contents.set(SLOT_CHEAT_STAIN, ClickableItem.of(DyeStation.getMagicStain().resetLore().build(),
					e -> setCheatDyeItem(contents, DyeStation.getMagicStain().build())));
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

	private void fillColors(ColorChoice.ChoiceType dyeType, int colorPage) {
		int row = 1;
		int col = 3;
		int index = 0;
		int countAdded = 0;


		switch (dyeType) {
			case DYE -> {
				for (ColorChoice.DyeChoice dyeChoice : ColorChoice.DyeChoice.values()) {
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

				for (ColorChoice.StainChoice stainChoice : ColorChoice.StainChoice.values()) {
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

				if (Math.ceil(ColorChoice.StainChoice.values().length / 9.0) > (colorPage + 1))
					contents.set(SLOT_STAIN_NEXT, ClickableItem.of(STAIN_NEXT, e -> reopenMenu(contents, colorPage + 1)));
				//e -> new DyeStationMenu(mode, data, colorPage + 1, false).open(viewer)));

				if (colorPage > 0)
					contents.set(SLOT_STAIN_PREVIOUS, ClickableItem.of(STAIN_PREVIOUS, e -> reopenMenu(contents, colorPage - 1)));
				//e -> new DyeStationMenu(mode, data, colorPage - 1, false).open(viewer)));
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
		reopenMenu(contents, dyePage);
	}

	private void reopenMenu(InventoryContents contents, int dyePage) {
		Optional<ClickableItem> itemOptional = contents.get(data.getInputSlot());
		ItemStack input = itemOptional.map(ClickableItem::getItem).orElse(null);

		Optional<ClickableItem> dyeOptional = contents.get(SLOT_DYE);
		ItemStack dye = dyeOptional.map(ClickableItem::getItem).orElse(null);
		if (CustomMaterial.of(dye) == ColorChoice.ChoiceType.DYE.getBottleMaterial())
			data.setDyeType(ColorChoice.ChoiceType.DYE);
		else if (CustomMaterial.of(dye) == ColorChoice.ChoiceType.STAIN.getBottleMaterial())
			data.setDyeType(ColorChoice.ChoiceType.STAIN);

		Optional<ClickableItem> resultOptional = contents.get(SLOT_RESULT);
		ItemStack result = resultOptional.map(ClickableItem::getItem).orElse(null);

		data.setInput(input);
		data.setDye(dye);
		data.setResult(result);
		data.setDyePage(dyePage);

		init();
	}

	private void fillChoices(ColorChoice.DyeChoice dyeChoice) {
		data.setDyeChoice(dyeChoice);
		int col = 1;
		List<ColorChoice.ColoredButton> choices = dyeChoice.getChoices();
		for (int i = 0; i < 7; i++) {
			if (i > choices.size() - 1)
				break;

			ColorChoice.ColoredButton button = choices.get(i);
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

	private ItemBuilder handleDye(ItemStack dye) {
		ItemBuilder builder = new ItemBuilder(dye.clone());

		List<String> lore = dye.getItemMeta().getLore();
		if (lore == null || lore.isEmpty())
			return builder;

		builder = decreaseUses(builder);
		if (builder.material() == Material.GLASS_BOTTLE)
			emptyColors();

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

		return ItemUtils.hasLore(dye, DyeStation.USAGE_LORE);
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

	//

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

		Player player = data.getPlayer();
		ItemStack result = resultOptional.get().getItem();
		if (mode != DyeStationMode.COSTUME)
			player.setItemOnCursor(result);

		ItemStack dye = dyeOptional.get().getItem();
		if (!data.isCheatMode()) {
			if (dye.getAmount() > 1) {
				ItemStack dyeExtra = dye.clone();
				dyeExtra.subtract();
				dye.setAmount(1);

				DecorationLang.debug(viewer, "Adding extra dye items");
				returnItems.add(dyeExtra);
			}

			DecorationLang.debug(viewer, "Adding original dye");
			dye = handleDye(dye).build();

			new SoundBuilder(Sound.ITEM_BOTTLE_EMPTY).location(player).pitch(RandomUtils.randomDouble(0.8, 1.2)).play();
			Tasks.wait(8, () -> new SoundBuilder(Sound.ITEM_BOTTLE_FILL).location(player).pitch(RandomUtils.randomDouble(0.8, 1.2)).play());
		}

		data.setInput(input);
		data.setDye(dye);
		data.setResult(new ItemStack(Material.AIR));
		data.onConfirm(returnItems);
		data.setColor(null);

		contents.set(SLOT_RESULT, ClickableItem.of(data.getInput(), e -> replaceItem(viewer, contents, e, SLOT_RESULT)));
		contents.set(SLOT_INPUT, ClickableItem.of(data.getInput(), e -> replaceItem(viewer, contents, e, SLOT_INPUT)));
		contents.set(SLOT_DYE, ClickableItem.of(data.getDye(), e -> replaceItem(viewer, contents, e, SLOT_DYE)));
	}

	@Data
	public static class DyeStationData {
		private final Player player;
		private final boolean cheatMode;
		private final boolean showButtons;
		private final SlotPos inputSlot;
		private final String title;
		private final Consumer<DyeStationData> onConfirm;
		private ItemStack input;
		private ItemStack dye;
		private ItemStack result;
		private ColorChoice.ChoiceType dyeType;
		private ColorChoice.DyeChoice dyeChoice;
		private int dyePage = 0;
		private Color color;
		private List<ItemStack> returnItems;

		@Builder
		public DyeStationData(Player player, boolean cheatMode, boolean showButtons,
							  SlotPos inputSlot, Consumer<DyeStationData> onConfirm, String title,
							  ColorChoice.ChoiceType dyeType, ItemStack input, ItemStack dye,
							  ItemStack result, ColorChoice.DyeChoice dyeChoice, Color color) {
			this.player = player;
			this.cheatMode = cheatMode;
			this.showButtons = showButtons;
			this.inputSlot = inputSlot;
			this.title = title;
			this.onConfirm = onConfirm;

			this.dyeType = dyeType;
			if (dyeType == null)
				this.dyeType = ColorChoice.ChoiceType.DYE;

			this.input = input;
			if (input == null)
				this.input = new ItemStack(Material.AIR);

			this.dye = dye;
			if (dye == null) {
				this.dye = new ItemStack(Material.AIR);
				if (cheatMode && showButtons)
					this.dye = DyeStation.getMagicDye().build();
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
}
