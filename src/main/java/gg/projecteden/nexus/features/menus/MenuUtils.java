package gg.projecteden.nexus.features.menus;

import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI.Response;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotIterator;
import gg.projecteden.nexus.features.menus.api.content.SlotIterator.Type;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.quests.CommonQuestItem;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomTexture;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Currency;
import gg.projecteden.nexus.utils.Currency.Price;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

public abstract class MenuUtils {

	public static final int COLUMNS = 9;

	public static SlotIterator innerSlotIterator(InventoryContents contents) {
		return innerSlotIterator(contents, SlotPos.of(0, 0));
	}

	public static SlotIterator innerSlotIterator(InventoryContents contents, SlotPos start) {
		final SlotIterator slotIterator = contents.newIterator(Type.HORIZONTAL, start);
		final int rows = contents.config().getRows();
		for (int i = 0; i < rows * COLUMNS; i++)
			if (i < COLUMNS || i % COLUMNS == 0 || (i + 1) % COLUMNS == 0 || i >= (rows - 1) * COLUMNS)
				slotIterator.blacklist(i);

		return slotIterator;
	}

	public static int calculateRows(int items) {
		return calculateRows(items, 2, 9);
	}

	public static int calculateRows(int items, int extraRows) {
		return calculateRows(items, extraRows, 9);
	}

	public static int calculateRows(int items, int extraRows, int itemsAcross) {
		return (int) Math.min(6, Math.ceil(Integer.valueOf(items).doubleValue() / itemsAcross) + extraRows);
	}

	public static List<String> getLocationLore(Location location) {
		if (location == null)
			return Collections.singletonList("null");

		return List.of("&3X:&e " + (int) location.getX(), "&3Y:&e " + (int) location.getY(), "&3Z:&e " + (int) location.getZ());
	}

	public static void handleException(Player player, String prefix, Throwable ex) {
		if (ex.getCause() != null && ex.getCause() instanceof NexusException)
			PlayerUtils.send(player, new JsonBuilder(prefix + "&c").next(((NexusException) ex.getCause()).getJson()));
		else if (ex instanceof NexusException)
			PlayerUtils.send(player, new JsonBuilder(prefix + "&c").next(((NexusException) ex).getJson()));
		else if (ex.getCause() != null && ex.getCause() instanceof EdenException)
			PlayerUtils.send(player, new JsonBuilder(prefix + "&c").next(ex.getCause().getMessage()));
		else if (ex instanceof EdenException)
			PlayerUtils.send(player, new JsonBuilder(prefix + "&c").next(ex.getMessage()));
		else {
			PlayerUtils.send(player, "&cAn internal error occurred while attempting to execute this command");
			ex.printStackTrace();
		}
	}

	public static void centerItems(ClickableItem[] items, InventoryContents contents, int row) {
		centerItems(items, contents, row, true);
	}

	public static void centerItems(ClickableItem[] items, InventoryContents contents, int row, boolean space) {
		if (items.length > 9)
			throw new InvalidInputException("Cannot center more than 9 items on one row");
		int[] even = {3, 5, 1, 7};
		int[] odd = {4, 2, 6, 0, 8};
		int[] noSpace = {4, 3, 5, 2, 6, 1, 7, 0, 8};
		if (items.length < 5 && space)
			if (items.length % 2 == 0)
				for (int i = 0; i < items.length; i++)
					contents.set(row, even[i], items[i]);
			else
				for (int i = 0; i < items.length; i++)
					contents.set(row, odd[i], items[i]);
		else
			for (int i = 0; i < items.length; i++)
				contents.set(row, noSpace[i], items[i]);
	}

	public static void openAnvilMenu(Player player, String text, BiFunction<Player, String, Response> onComplete, Consumer<Player> onClose) {
		new AnvilGUI.Builder()
			.text(text)
			.onComplete(onComplete)
			.onClose(onClose)
			.plugin(Nexus.getInstance())
			.open(player);
	}

	@Builder
	@RequiredArgsConstructor
	public static class AnvilMenu<T> {
		private @NotNull
		final InventoryProvider menu;
		private @NotNull
		final ItemClickData click;
		private @NotNull
		final Supplier<@Nullable ?> getter;
		private @NotNull
		final Consumer<@Nullable T> setter;
		private @Nullable
		final Predicate<@NotNull String> checker;
		private @NotNull
		final Function<@NotNull String, @Nullable T> converter;
		/**
		 * Runs a method after the {@link #setter} is called, i.e. {@link Arena#write()}
		 */
		private @Nullable
		final Runnable writer;
		private @NotNull
		final String error;

		public void open() {
			openAnvilMenu(click.getPlayer(), String.valueOf(getter.get()), (p, text) -> {
				try {
					if (checker != null && checker.test(text)) {
						setter.accept(converter.apply(text));
						if (writer != null)
							writer.run();
						return Response.close();
					}
				} catch (Exception ignored) {
				}
				PlayerUtils.send(p, error);
				return Response.close();
			}, p -> Tasks.wait(1, () -> menu.open(p)));
		}

		public static class AnvilMenuBuilder<T> {
			public void open() {
				build().open();
			}

		}

		public static class IntegerBuilder extends AnvilMenuBuilder<Integer> {
			public IntegerBuilder() {
				super();
				checker(Utils::isInt);
				converter(Integer::parseInt);
				error("Input must be an integer");
			}

			/**
			 * Sets the {@link #converter} to ensure inputs are greater than or equal to zero
			 */
			@Contract("-> this")
			public IntegerBuilder nonNegativeChecker() {
				checker(text -> Utils.isInt(text) && Integer.parseInt(text) >= 0);
				error("Input must be a non-negative integer");
				return this;
			}

			/**
			 * Sets the {@link #converter} to ensure inputs are greater than or equal to zero
			 */
			@Contract("-> this")
			public IntegerBuilder positiveChecker() {
				checker(text -> Utils.isInt(text) && Integer.parseInt(text) > 0);
				error("Input must be a positive integer");
				return this;
			}

		}

	}

	@Rows(3)
	@Builder(buildMethodName = "_build")
	@AllArgsConstructor
	public static class ConfirmationMenu extends InventoryProvider {
		@Getter
		@Default
		private final String title = CustomTexture.GUI_CONFIRMATION.getMenuTexture() + "&4Are you sure?";
		@Default
		private final String cancelText = "&cNo";
		private final List<String> cancelLore;
		@Default
		private final ItemStack cancelItem = CustomMaterial.GUI_CLOSE.getNoNamedItem().dyeColor(ColorType.RED).build();
		@Default
		private final Consumer<ItemClickData> onCancel = (e) -> e.getPlayer().closeInventory();
		@Default
		private final String confirmText = "&aYes";
		private final List<String> confirmLore;
		@Default
		private final ItemStack confirmItem = CustomMaterial.GUI_CHECK.getNoNamedItem().dyeColor(ColorType.LIGHT_GREEN).build();
		@NonNull
		private final Consumer<ItemClickData> onConfirm;
		private final Consumer<ItemClickData> onFinally;
		private final Consumer<InventoryContents> additionalContents;


		public static class ConfirmationMenuBuilder {

			public ConfirmationMenuBuilder titleWithSlot(String title) {
				this.title$value = CustomTexture.GUI_CONFIRMATION_SLOT.getMenuTexture() + "&4" + title;
				this.title$set = true;
				return this;
			}

			public ConfirmationMenuBuilder title(String title) {
				this.title$value = CustomTexture.GUI_CONFIRMATION.getMenuTexture() + "&4" + title;
				this.title$set = true;
				return this;
			}

			public ConfirmationMenuBuilder displayItem(ItemStack item) {
				this.additionalContents = contents -> contents.set(0, 4, ClickableItem.empty(item));
				return this;
			}

			public void open(Player player) {
				Tasks.sync(() -> _build().open(player));
			}

			@Deprecated
			public ConfirmationMenu build() {
				throw new UnsupportedOperationException("Use open(player)");
			}

		}

		@Override
		public void init() {
			ItemBuilder cancel = new ItemBuilder(cancelItem).name(cancelText).lore(cancelLore);
			ItemBuilder confirm = new ItemBuilder(confirmItem).name(confirmText).lore(confirmLore);

			contents.set(1, 2, ClickableItem.of(cancel.build(), e -> {
				try {
					if (onCancel != null)
						onCancel.accept(e);

					if (title.equals(e.getPlayer().getOpenInventory().getTitle()))
						e.getPlayer().closeInventory();

					if (onFinally != null)
						onFinally.accept(e);
				} catch (Exception ex) {
					MenuUtils.handleException(viewer, "", ex);
				}
			}));

			contents.set(1, 6, ClickableItem.of(confirm.build(), e -> {
				try {
					onConfirm.accept(e);

					if (colorize(title).equals(e.getPlayer().getOpenInventory().getTitle()))
						e.getPlayer().closeInventory();

					if (onFinally != null)
						onFinally.accept(e);
				} catch (Exception ex) {
					MenuUtils.handleException(viewer, "", ex);
				}
			}));

			if (additionalContents != null)
				additionalContents.accept(contents);
		}

	}


	// TODO: JOBS - Temporary menu until jobs are complete
	@Rows(3)
	@Builder(buildMethodName = "_build")
	@AllArgsConstructor
	public static class NPCShopMenu extends InventoryProvider {
		@Getter
		@Default
		private final String title = "Shop";
		private final int npcId;
		private final List<Product> products;
		@Nullable
		private final ShopGroup shopGroup;
		private final boolean closeAfterPurchase;

		private final BankerService bankerService = new BankerService();

		public static class NPCShopMenuBuilder {
			public void open(Player player) {
				Tasks.sync(() -> _build().open(player));
			}

			@Deprecated
			public NPCShopMenu build() {
				throw new UnsupportedOperationException("Use open(player)");
			}

		}

		@Override
		public void init() {
			addCloseItem();

			final List<ClickableItem> items = new ArrayList<>();

			products.forEach(product -> {
				ItemStack item = product.getItemStack();
				Currency currency = product.getCurrency();
				Price price = product.getPrice();
				BiConsumer<Player, InventoryProvider> onPurchase = product.getOnPurchase();

				if (PlayerUtils.playerHas(viewer, CommonQuestItem.DISCOUNT_CARD.getCustomMaterial())) {
					Price newPrice = price.clone();
					newPrice.applyDiscount(CommonQuestItem.DISCOUNT_CARD_PERCENT);
					price = newPrice;
				}
				Price finalPrice = price;

				boolean canAfford = currency.canAfford(viewer, finalPrice, shopGroup);
				String priceLore = currency.getPriceLore(finalPrice, canAfford);

				final ItemBuilder displayItem = new ItemBuilder(product.getDisplayItemStack()).lore(priceLore);


				items.add(ClickableItem.of(displayItem, e -> {
					try {
						if (currency.canAfford(viewer, finalPrice, shopGroup)) {
							ConfirmationMenu.builder()
								.titleWithSlot("&4Are you sure?")
								.displayItem(displayItem.build())
								.onConfirm(e2 -> {
									try {
										if (currency.canAfford(viewer, finalPrice, shopGroup)) {
											currency.withdraw(viewer, finalPrice, shopGroup, product);

											if (product.isVirtual()) {
												if (onPurchase != null)
													onPurchase.accept(viewer, this);
												return;
											}

											currency.log(viewer, finalPrice, product, shopGroup);

											if (onPurchase != null)
												onPurchase.accept(viewer, this);

											PlayerUtils.giveItem(viewer, item);
											// TODO DECORATION: PLAY SOUND
										} else {
											throw new InvalidInputException("You cannot afford that!");
										}
									} catch (Exception ex) {
										MenuUtils.handleException(viewer, StringUtils.getPrefix("NPCShopMenu"), ex);
									}
								})
								.onFinally(e2 -> {
									if (closeAfterPurchase)
										close();
									else
										refresh();
								})
								.open(viewer);
						} else {
							throw new InvalidInputException("You cannot afford that!");
						}
					} catch (Exception ex) {
						MenuUtils.handleException(viewer, StringUtils.getPrefix("NPCShopMenu"), ex);
					}
				}));
			});

			paginator().items(items).perPage(18).build();
		}

		@Data
		@AllArgsConstructor
		public static class Product {
			@Nullable
			ItemStack itemStack;
			@Nullable
			ItemStack displayItemStack;
			boolean virtual = false;

			Currency currency;
			@Nullable
			Currency.Price price;

			@Nullable
			BiConsumer<Player, InventoryProvider> onPurchase;

			public Product() {
				this(null, null);
			}

			public Product(ItemBuilder itemBuilder) {
				this(itemBuilder.build());
			}

			public Product(CustomMaterial material) {
				this(material.getItem());
			}

			public Product(Material material) {
				this(new ItemStack(material));
			}

			public Product(ItemStack itemStack) {
				this(itemStack, null);
			}

			public Product(@Nullable ItemStack itemStack, @Nullable ItemStack displayItemStack) {
				this.itemStack = itemStack;
				this.displayItemStack = displayItemStack;
			}

			public static Product free(CustomMaterial material) {
				return free(material.getItem());
			}

			public static Product free(Material material) {
				return free(new ItemStack(material));
			}

			public static Product free(ItemBuilder itemBuilder) {
				return free(itemBuilder.build());
			}

			public static Product free(ItemStack itemStack) {
				return new Product(itemStack, null, false, Currency.FREE, Price.of(0), null);
			}

			public static Product virtual(CustomMaterial material, Currency currency, Price price, BiConsumer<Player, InventoryProvider> onPurchase) {
				return virtual(material.getItem(), currency, price, onPurchase);
			}

			public static Product virtual(Material material, Currency currency, Price price, BiConsumer<Player, InventoryProvider> onPurchase) {
				return virtual(new ItemStack(material), currency, price, onPurchase);
			}

			public static Product virtual(ItemBuilder itemBuilder, Currency currency, Price price, BiConsumer<Player, InventoryProvider> onPurchase) {
				return virtual(itemBuilder.build(), currency, price, onPurchase);
			}

			public static Product virtual(ItemStack itemStack, Currency currency, Price price, BiConsumer<Player, InventoryProvider> onPurchase) {
				return new Product(null, itemStack, true, currency, price, onPurchase);
			}

			//


			public Product itemStack(ItemStack itemstack) {
				this.itemStack = itemstack;
				return this;
			}

			public Product displayItemStack(ItemStack displayItemStack) {
				this.displayItemStack = displayItemStack;
				return this;
			}

			public Product price(Currency currency, Price price) {
				this.currency = currency;
				this.price = price;
				return this;
			}

			public Product price(Currency currency, double price) {
				this.currency = currency;
				this.price = Price.of(price);
				return this;
			}

			public Product onPurchase(BiConsumer<Player, InventoryProvider> consumer) {
				this.onPurchase = consumer;
				return this;
			}

			public ItemStack getDisplayItemStack() {
				return displayItemStack == null ? itemStack : displayItemStack;
			}

		}

	}

	public static void formatInventoryContents(InventoryContents contents, ItemStack[] inventory) {
		formatInventoryContents(contents, inventory, true);
	}

	public static void formatInventoryContents(InventoryContents contents, ItemStack[] inventory, boolean editable) {
		ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		contents.set(4, 4, ClickableItem.empty(redPane.clone(), "&eArmor ➝"));
		contents.set(4, 1, ClickableItem.empty(redPane.clone(), "&e← Offhand"));
		contents.outline(4, 2, 4, 3, ClickableItem.empty(redPane.clone(), "&e⬇ Hot Bar ⬇"));

		if (inventory == null || inventory.length == 0)
			return;

		// Hotbar
		for (int i = 0; i < 9; i++) {
			if (editable)
				contents.setEditable(5, i, true);

			if (inventory[i] == null)
				continue;

			contents.set(5, i, ClickableItem.empty(inventory[i]));
		}

		// Inventory
		int row = 1;
		int column = 0;
		for (int i = 9; i < 36; i++) {
			if (editable)
				contents.setEditable(row, column, true);

			if (inventory[i] != null)
				contents.set(row, column, ClickableItem.empty(inventory[i]));

			if (column != 8)
				++column;
			else {
				column = 0;
				++row;
			}
		}

		// Offhand
		if (editable)
			contents.setEditable(4, 0, true);

		if (inventory[40] != null)
			contents.set(4, 0, ClickableItem.empty(inventory[40]));

		// Armor
		column = 8;
		for (int i = 36; i < 40; i++) {
			if (editable)
				contents.setEditable(4, column, true);

			if (inventory[i] != null)
				contents.set(4, column, ClickableItem.empty(inventory[i]));
			--column;
		}
	}

}
