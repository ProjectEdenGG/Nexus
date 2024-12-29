package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.SignMenuFactory;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.shops.ShopUtils;
import gg.projecteden.nexus.features.shops.Shops;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.ExchangeType;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

@Title("&0Add Item")
public class ExchangeConfigProvider extends ShopProvider {
	private Product product;
	private final AtomicReference<ItemStack> item = new AtomicReference<>();
	private final AtomicReference<ItemStack> priceItem = new AtomicReference<>();
	private double price = -1;
	private ExchangeType exchangeType = ExchangeType.SELL;
	private double stock;
	private boolean allowEditItem = true;

	private final ItemStack less8 = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).amount(8).name("&cDecrease amount by 8").build();
	private final ItemStack less1 = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("&cDecrease amount").build();
	private final ItemStack more1 = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("&aIncrease amount").build();
	private final ItemStack more8 = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).amount(8).name("&aIncrease amount by 8").build();

	public ExchangeConfigProvider(ShopProvider previousMenu) {
		this(previousMenu, null);
	}

	public ExchangeConfigProvider(ShopProvider previousMenu, Product product) {
		this.previousMenu = previousMenu;
		this.product = product;
		if (product != null) {
			allowEditItem = false;
			exchangeType = product.getExchangeType();
			item.set(product.getItem());
			stock = product.getStock();
			if (product.getPrice() instanceof ItemStack)
				priceItem.set((ItemStack) product.getPrice());
			else
				price = (double) product.getPrice();
		}
	}

	@Override
	public void init() {
		super.init();

		addItemSelector(viewer, contents, 1, item, allowEditItem);
		addExchangeControl(viewer, contents);
		if (exchangeType == ExchangeType.TRADE)
			addItemSelector(viewer, contents, 3, priceItem);
		else
			addMoneyEditor(viewer, contents);

		addConfirmButton(viewer, contents);
	}

	public void addConfirmButton(Player player, InventoryContents contents) {
		contents.set(5, 4, ClickableItem.empty(new ItemBuilder(Material.LIGHT_GRAY_CONCRETE_POWDER)
				.name("&7Configure your exchange before continuing").build()));

		if (item.get() != null) {
			ItemBuilder confirm = new ItemBuilder(Material.LIME_CONCRETE_POWDER);
			if (exchangeType == ExchangeType.TRADE) {
				if (priceItem.get() != null) {
					confirm.name("&3Trade &e" + StringUtils.pretty(item.get())).lore("&3for &e" + StringUtils.pretty(priceItem.get()));
					if (product == null)
						product = new Product(player.getUniqueId(), shopGroup, item.get(), stock, exchangeType, priceItem.get());
					else {
						product.setItem(item.get());
						product.setStock(stock);
						product.setExchangeType(exchangeType);
						product.setPrice(priceItem.get());
					}
				}
			} else
				if (price >= 0) {
					if (exchangeType == ExchangeType.BUY)
						confirm.name("&3Buy &e" + StringUtils.pretty(item.get()) + " &3from").lore("&3customers for &e" + ShopUtils.prettyMoney(price));
					else if (exchangeType == ExchangeType.SELL)
						confirm.name("&3Sell &e" + StringUtils.pretty(item.get()) + " &3to").lore("&3customers for &e" + ShopUtils.prettyMoney(price));
					if (product == null)
						product = new Product(player.getUniqueId(), shopGroup, item.get(), stock, exchangeType, price);
					else {
						product.setItem(item.get());
						product.setStock(stock);
						product.setExchangeType(exchangeType);
						product.setPrice(price);
					}
				}

			if (product != null)
				contents.set(5, 4, ClickableItem.of(confirm.build(), e -> {
					Shop shop = service.get(player);
					if (allowEditItem)
						shop.getProducts().add(product);
					service.save(shop);
					if (previousMenu instanceof EditProductProvider)
						previousMenu.open(player);
					else
						new EditProductProvider(previousMenu, product).open(player);
				}));
		}
	}

	private void addMoneyEditor(Player player, InventoryContents contents) {
		ItemBuilder item = new ItemBuilder(Material.GOLD_INGOT);

		if (price < 0)
			item.name("&eClick to specify a dollar amount");
		else
			item.name("&e" + StringUtils.camelCase(ShopUtils.prettyMoney(price)));

		contents.set(3, 4, ClickableItem.of(item.build(), e -> Nexus.getSignMenuFactory()
				.lines("", "^ ^ ^ ^ ^ ^", "Enter a", "dollar amount")
				.prefix(Shops.PREFIX)
				.onError(() -> open(player))
				.response(lines -> {
					if (lines[0].length() > 0) {
						String input = lines[0].replaceAll("[^\\d.]+", "");
						if (!Utils.isDouble(input))
							throw new InvalidInputException("Could not parse &e" + lines[0] + " &cas a dollar amount");
						double price = new BigDecimal(input).setScale(2, RoundingMode.HALF_UP).doubleValue();
						if (price < 0)
							throw new InvalidInputException("Dollar amount must be $0 or greater");
						this.price = price;
					}
					open(player);
				}).open(player)));
		}

	private void addExchangeControl(Player player, InventoryContents contents) {
		ItemBuilder item = new ItemBuilder(Material.PAPER).name("&6Action: ")
				.lore("&7⬇ " + StringUtils.camelCase(exchangeType.previousWithLoop().name()))
				.lore("&e⬇ " + StringUtils.camelCase(exchangeType.name()))
				.lore("&7⬇ " + StringUtils.camelCase(exchangeType.nextWithLoop().name()));
		contents.set(2, 4, ClickableItem.of(item.build(), e -> {
			exchangeType = exchangeType.nextWithLoop();
			open(player);
		}));
	}

	public void addItemSelector(Player player, InventoryContents contents, int row, AtomicReference<ItemStack> itemStack) {
		addItemSelector(player, contents, row, itemStack, true);
	}

	public void addItemSelector(Player player, InventoryContents contents, int row, AtomicReference<ItemStack> itemStack, boolean allowEditItem) {
		ItemStack placeholder = new ItemBuilder(Material.BLACK_STAINED_GLASS).name("&ePlace your item here").lore("&7or click to search for an item").build();

		if (!allowEditItem)
			contents.set(row, 4, ClickableItem.empty(itemStack.get()));
		else {
			Consumer<ItemClickData> action = e -> {
				((InventoryClickEvent) e.getEvent()).setCancelled(true);
				if (!Nullables.isNullOrAir(player.getItemOnCursor())) {
					try {
						ItemStack item = player.getItemOnCursor();
						if (new ItemBuilder(item).isNot(ItemSetting.TRADEABLE))
							throw new InvalidInputException("You can not trade that item in shops");

						itemStack.set(item);
						PlayerUtils.giveItem(player, itemStack.get().clone());
						player.setItemOnCursor(null);
						open(player);
					} catch (Exception ex) {
						PlayerUtils.send(player, Shops.PREFIX + "&c" + ex.getMessage());
						open(player);
					}
				} else if (contents.get(row, 4).isPresent() && contents.get(row, 4).get().getItem().equals(placeholder)) {
					Nexus.getSignMenuFactory()
							.lines("", SignMenuFactory.ARROWS, "Enter a", "search term")
							.prefix(Shops.PREFIX)
							.onError(() -> open(player))
							.response(lines -> {
								if (lines[0].length() > 0) {
									Function<Material, Boolean> filter = material -> material.name().toLowerCase().contains(lines[0].toLowerCase());
									new ItemSearchProvider(this, filter, onChoose -> {
										itemStack.set(new ItemStack(onChoose.getItem().getType()));
										open(player);
									}).open(player);
								} else
									open(player);
							}).open(player);
				} else {
					itemStack.set(null);
					open(player);
				}
			};

			if (itemStack.get() != null)
				contents.set(row, 4, ClickableItem.of(itemStack.get(), action));
			else
				contents.set(row, 4, ClickableItem.of(placeholder, action));
		}

		if (contents.get(row, 4).isPresent() && contents.get(row, 4).get().getItem() != null && contents.get(row, 4).get().getItem().equals(placeholder)) {
			contents.set(row, 2, ClickableItem.empty(less8));
			contents.set(row, 3, ClickableItem.empty(less1));
			contents.set(row, 5, ClickableItem.empty(more1));
			contents.set(row, 6, ClickableItem.empty(more8));
		} else {
			contents.set(row, 2, ClickableItem.of(less8, e2 -> contents.get(row, 4).ifPresent(i -> {
				ItemStack item = i.getItem();
				item.setAmount(Math.max(1, Math.min(item.getType().getMaxStackSize(), item.getAmount() == 64 ? 56 : item.getAmount() - 8)));
				open(player);
			})));
			contents.set(row, 3, ClickableItem.of(less1, e2 -> contents.get(row, 4).ifPresent(i -> {
				ItemStack item = i.getItem();
				item.setAmount(Math.max(1, Math.min(item.getType().getMaxStackSize(), item.getAmount() - 1)));
				open(player);
			})));
			contents.set(row, 5, ClickableItem.of(more1, e2 -> contents.get(row, 4).ifPresent(i -> {
				ItemStack item = i.getItem();
				item.setAmount(Math.min(64, Math.min(item.getType().getMaxStackSize(), item.getAmount() + 1)));
				open(player);
			})));
			contents.set(row, 6, ClickableItem.of(more8, e2 -> contents.get(row, 4).ifPresent(i -> {
				ItemStack item = i.getItem();
				item.setAmount(Math.min(64, Math.min(item.getType().getMaxStackSize(), item.getAmount() == 1 ? 8 : item.getAmount() + 8)));
				open(player);
			})));
		}
	}

}
