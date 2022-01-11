package gg.projecteden.nexus.features.custombenches;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.ItemClickData;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import gg.projecteden.nexus.features.custombenches.DyeStation.DyeStationMenu.DyeChoice;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class DyeStation extends CustomBench {

	private static final String USAGE_LORE = "&3Used in Dye Station";
	private static final int MAX_USES = 5;
	private static final String USES_LORE = "&3Uses: &e";

	private static final ItemBuilder MAGIC_DYE = new ItemBuilder(Material.PAPER)
		.customModelData(DyeType.DYE.getBottleModelData())
		.name(Gradient.of(List.of(ChatColor.RED, ChatColor.YELLOW, ChatColor.AQUA)).apply("Magic Dye"))
		.lore(USAGE_LORE, USES_LORE + MAX_USES);

	private static final ItemBuilder MAGIC_STAIN = new ItemBuilder(Material.PAPER)
		.customModelData(DyeType.STAIN.getBottleModelData())
		.name(Gradient.of(List.of(ChatColor.of("#e0a175"), ChatColor.of("#5c371d"))).apply("Magic Stain"))
		.lore(USAGE_LORE, USES_LORE + MAX_USES);

	private static final ItemBuilder DYE_STATION = new ItemBuilder(Material.CRAFTING_TABLE)
		.customModelData(1)
		.name("Dye Station");


	public static ItemBuilder getMagicDye() {
		return MAGIC_DYE.clone();
	}

	public static ItemBuilder getMagicStain() {
		return MAGIC_STAIN.clone();
	}

	public static ItemBuilder getDyeStation() {
		return DYE_STATION.clone();
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

	@Getter
	@AllArgsConstructor
	public enum DyeType {
		DYE(5, 1),
		STAIN(6, 2),
		;

		private final int bottleModelData;
		private final int buttonModelData;

		public ItemBuilder getItem() {
			return new ItemBuilder(Material.LEATHER_HORSE_ARMOR).customModelData(bottleModelData);
		}

		public ItemBuilder getButton() {
			return new ItemBuilder(Material.LEATHER_HORSE_ARMOR).customModelData(buttonModelData);
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class DyeStationMenu extends MenuUtils implements InventoryProvider, Listener {
		private DyeStationData data;

		private static final SlotPos SLOT_INPUT = new SlotPos(1, 1);
		private static final SlotPos SLOT_COSTUME = new SlotPos(2, 1);
		private static final SlotPos SLOT_DYE = new SlotPos(3, 1);
		private static final SlotPos SLOT_RESULT = new SlotPos(2, 7);
		private static final SlotPos SLOT_CHEAT_DYE = new SlotPos(0, 3);
		private static final SlotPos SLOT_CHEAT_STAIN = new SlotPos(0, 5);

		public void open(Player player) {
			open(getData(player, false));
		}

		public void openCheat(Player player) {
			open(getData(player, true));
		}

		private DyeStationData getData(Player player, boolean cheatMode) {
			return DyeStationData.builder()
				.player(player)
				.cheatMode(cheatMode)
				.showButtons(true)
				.inputSlot(SLOT_INPUT)
				.title("섈")
				.onConfirm(data -> PlayerUtils.giveItems(player, data.getReturnItems()))
				.build();
		}

		public void openCostume(CostumeUser user, Costume costume, Consumer<DyeStationData> onConfirm) {
			open(DyeStationData.builder()
				.player(user.getOnlinePlayer())
				.cheatMode(true)
				.input(user.getCostumeDisplayItem(costume))
				.inputSlot(SLOT_COSTUME)
				.title("膛")
				.onConfirm(onConfirm)
				.build());
		}

		public void open(DyeStationData data) {
			getInv(data).open(data.getPlayer());
		}

		private SmartInventory getInv(DyeStationData data) {
			return SmartInventory.builder()
				.title(StringUtils.colorize("&fꈉ" + data.getTitle()))
				.size(6, 9)
				.provider(new DyeStationMenu(data))
				.closeable(false)
				.build();
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			contents.set(0, ClickableItem.from(closeItem(), e -> {
				contents.get(data.getInputSlot()).ifPresent(clickableItem -> PlayerUtils.giveItem(player, clickableItem.getItem()));
				contents.get(SLOT_DYE).ifPresent(clickableItem -> {
					if (!data.isCheatMode())
						PlayerUtils.giveItem(player, clickableItem.getItem());
				});

				getInv(data).close(player);
			}));

			contents.set(data.getInputSlot(), ClickableItem.from(data.getInput(), e -> replaceItem(player, contents, e, data.getInputSlot())));
			contents.set(SLOT_DYE, ClickableItem.from(data.getDye(), e -> replaceItem(player, contents, e, SLOT_DYE)));

			if (data.getColor() != null)
				setResultItem(data.getColor(), contents);

			fillColors(data.getDyeType(), contents);

			if (data.getDyeType().equals(DyeType.DYE) && data.getDyeChoice() != null)
				fillChoices(data.getDyeChoice(), contents);

			if (data.isCheatMode() && data.isShowButtons()) {
				contents.set(SLOT_CHEAT_DYE, ClickableItem.from(getMagicDye().resetLore().build(),
					e -> setDyeItem(contents, getMagicDye().build())));
				contents.set(SLOT_CHEAT_STAIN, ClickableItem.from(getMagicStain().resetLore().build(),
					e -> setDyeItem(contents, getMagicStain().build())));
			}
		}

		private void setDyeItem(InventoryContents contents, ItemStack item) {
			contents.set(SLOT_DYE, ClickableItem.empty(item));
			reopenMenu(contents);
		}

		private void fillColors(DyeType dyeType, InventoryContents contents) {
			int row = 1;
			int col = 3;
			int count = 0;

			switch (dyeType) {
				case DYE -> {
					for (DyeChoice dyeChoice : DyeChoice.values()) {
						String itemName = StringUtils.camelCase(dyeChoice) + "s";
						contents.set(row, col++, ClickableItem.from(dyeChoice.getItem(itemName), e -> fillChoices(dyeChoice, contents)));

						if (++count == 3) {
							++row;
							col = 3;
							count = 0;
						}
					}
				}

				case STAIN -> {
					for (StainChoice stainChoice : StainChoice.values()) {
						String itemName = StringUtils.camelCase(stainChoice);
						Color color = stainChoice.getButton().getColor();
						contents.set(row, col++, ClickableItem.from(stainChoice.getItem(itemName), e -> setResultItem(color, contents)));

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
			boolean emptyCursor = Nullables.isNullOrAir(cursorItem);
			ItemStack slotItem = e.getItem();

			if (Nullables.isNullOrAir(slotItem) && emptyCursor)
				return;

			// if slot is empty
			if (Nullables.isNullOrAir(slotItem)) {
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
			if (CustomModelData.of(dye) == DyeType.DYE.getBottleModelData())
				data.setDyeType(DyeType.DYE);
			else if (CustomModelData.of(dye) == DyeType.STAIN.getBottleModelData())
				data.setDyeType(DyeType.STAIN);

			Optional<ClickableItem> resultOptional = contents.get(SLOT_RESULT);
			ItemStack result = resultOptional.map(ClickableItem::getItem).orElse(null);

			data.setInput(input);
			data.setDye(dye);
			data.setResult(result);

			open(data);
		}

		private void fillChoices(DyeChoice dyeChoice, InventoryContents contents) {
			data.setDyeChoice(dyeChoice);
			int col = 1;
			List<ColoredButton> choices = dyeChoice.getChoices();
			for (int i = 0; i < 7; i++) {
				if (i > choices.size() - 1)
					break;

				ColoredButton button = choices.get(i);
				contents.set(5, col, ClickableItem.from(button.getItem(data.getDyeType(), "Select Shade"),
					e -> setResultItem(button.getColor(), contents)));
				++col;
			}
		}

		private void setResultItem(Color color, InventoryContents contents) {
			if (color == null)
				return;

			boolean validInput = isValidInput(contents);
			boolean validDye = isValidDyeType(contents);

			if (!validDye || !validInput)
				return;

			ItemStack result = new ItemBuilder(contents.get(data.getInputSlot()).orElseThrow().getItem()).dyeColor(color).build();

			data.setColor(color);
			data.setResult(result);
			contents.set(SLOT_RESULT, ClickableItem.from(result, e -> confirm(contents)));
		}

		private void confirm(InventoryContents contents) {
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

					returnItems.add(dyeExtra);
				}

				returnItems.add(handleDye(dye).build());

				new SoundBuilder(Sound.ITEM_BOTTLE_EMPTY).location(player).pitch(RandomUtils.randomDouble(0.8, 1.2)).play();
				Tasks.wait(8, () -> new SoundBuilder(Sound.ITEM_BOTTLE_FILL).location(player).pitch(RandomUtils.randomDouble(0.8, 1.2)).play());
			}

			data.setInput(input);
			data.setDye(dye);
			data.setResult(result);
			data.onConfirm(returnItems);

			getInv(data).close(player);
		}

		private ItemBuilder handleDye(ItemStack dye) {
			ItemBuilder builder = new ItemBuilder(dye);

			List<String> lore = dye.getItemMeta().getLore();
			if (lore == null || lore.isEmpty())
				return builder;

			List<String> newLore = new ArrayList<>();
			for (String line : lore) {
				String _line = stripColor(line);
				if (_line.contains(stripColor(USES_LORE))) {
					int uses = Integer.parseInt(_line.replaceAll("Uses: ", ""));
					--uses;

					if (uses == 0)
						builder = new ItemBuilder(Material.GLASS_BOTTLE);

					newLore.add(USES_LORE + uses);
				} else {
					newLore.add(line);
				}
			}

			if (!builder.material().equals(Material.GLASS_BOTTLE))
				builder.setLore(newLore);

			return builder;
		}

		private boolean isValidDyeType(InventoryContents contents) {
			if (data.isCheatMode())
				return true;

			Optional<ClickableItem> dyeOptional = contents.get(SLOT_DYE);
			if (dyeOptional.isEmpty())
				return false;

			ItemStack dye = dyeOptional.get().getItem();
			if (Nullables.isNullOrAir(dye))
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
			if (Nullables.isNullOrAir(input))
				return false;

			return MaterialTag.DYEABLE.isTagged(input);
		}

		@Getter
		public enum StainChoice {
			BIRCH("#FEE496"),
			OAK("#F4C57A"),
			JUNGLE("#EFA777"),
			SPRUCE("#AD7B49"),
			DARK_OAK("#664421"),
			CRIMSON("#924967"),
			ACACIA("#F18648"),
			WARPED("#2FA195"),
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
		}

		@Getter
		public enum DyeChoice {
			RED("#FF0000", List.of("#FF756B", "#FF5E52", "#FF4233", "#FF0000", "#C70F00", "#9C0B00", "#6E0800")),
			ORANGE("#FF7F00", List.of("#FFBF6B", "#FFB552", "#FFA833", "#FF7F00", "#C77200", "#9C5900", "#6E3F00")),
			YELLOW("#FEFF00", List.of("#F5FF6B", "#F2FF52", "#EFFF33", "#FEFF00", "#B7C700", "#909C00", "#656E00")),
			PINK("#FF54BD", List.of("#FF9CD3", "#FF8BCA", "#FF76C0", "#FF54BD", "#FF2E9F", "#DB3690", "#B33F7E")),
			WHITE("#FFFFFF", List.of("#FFFFFF", "#C7C7C7", "#8F8F8F", "#6E6E6E", "#525252", "#333333", "#222222")),
			GREEN("#7FFF00", List.of("#ABFF6B", "#9CFF52", "#89FF33", "#7FFF00", "#54C700", "#429C00", "#2E6E00")),
			PURPLE("#A900FF", List.of("#D76BFF", "#D152FF", "#CA33FF", "#A900FF", "#9300C7", "#73009C", "#51006E")),
			BLUE("#0040FF", List.of("#6B86FF", "#5271FF", "#3357FF", "#0040FF", "#0023C7", "#001C9C", "#004A6E")),
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
}
