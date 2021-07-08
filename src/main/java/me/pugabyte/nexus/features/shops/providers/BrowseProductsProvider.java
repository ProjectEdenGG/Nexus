package me.pugabyte.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.Filter;
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.FilterEmptyStock;
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.FilterExchangeType;
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.FilterMarketItems;
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.FilterRequiredType;
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.FilterSearchType;
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.FilterType;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.Product;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static me.pugabyte.nexus.features.shops.Shops.PREFIX;
import static me.pugabyte.nexus.utils.ItemUtils.getRawShulkerContents;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;

public class BrowseProductsProvider extends _ShopProvider {
	@Getter
	protected List<Filter> filters;
	protected Shop shop;

	public BrowseProductsProvider(_ShopProvider previousMenu) {
		this(previousMenu, null, new ArrayList<>());
	}

	public BrowseProductsProvider(_ShopProvider previousMenu, Shop shop) {
		this(previousMenu, shop, new ArrayList<>());
	}

	public BrowseProductsProvider(_ShopProvider previousMenu, Filter filter) {
		this(previousMenu, null, Collections.singletonList(filter));
	}

	public BrowseProductsProvider(_ShopProvider previousMenu, Shop shop, Filter filter) {
		this(previousMenu, shop, Collections.singletonList(filter));
	}

	public BrowseProductsProvider(_ShopProvider previousMenu, Shop shop, List<Filter> filters) {
		this.previousMenu = previousMenu;
		if (previousMenu != null && previousMenu.getPreviousMenu() != null && previousMenu.getPreviousMenu().getPreviousMenu() != null
				&& previousMenu.getPreviousMenu().getClass() == this.getClass())
			this.previousMenu = previousMenu.getPreviousMenu().getPreviousMenu();
		this.shop = shop;
		this.filters = new ArrayList<>(filters);
		addDefaultFilters();
	}

	private void addDefaultFilters() {
		if (getFilter(FilterEmptyStock.class) == null)
			filters.add(FilterEmptyStock.HIDDEN.get());
		if (!(this instanceof BrowseMarketProvider))
			filters.add(FilterMarketItems.HIDDEN.get());
	}

	@Override
	public void open(Player viewer, int page) {
		open(viewer, page, this, "&0Browse Items");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		addFilters(player, contents);
		addItems(player, contents);
	}

	public void addFilters(Player player, InventoryContents contents) {
		addSearchFilter(player, contents);
		addStockFilter(player, contents);
		addExchangeFilter(player, contents);
		addMarketFilter(player, contents);
	}

	public void addSearchFilter(Player player, InventoryContents contents) {
		Filter searchFilter = getFilter(FilterSearchType.class);
		if (searchFilter != null) {
			ItemStack search = new ItemBuilder(Material.COMPASS).name("&6Current filter: &e" + searchFilter.getMessage())
					.lore("").lore("&7Click to remove filter").glow().build();
			contents.set(0, 4, ClickableItem.from(search, e -> {
				filters.remove(searchFilter);
				open(player, contents.pagination().getPage());
			}));
		} else
			contents.set(0, 4, ClickableItem.from(nameItem(Material.COMPASS, "&6Filter Items"), e -> new SearchProductsProvider(this).open(player)));
	}

	public void addStockFilter(Player player, InventoryContents contents) {
		Filter stockFilter = getFilter(FilterEmptyStock.class);
		FilterEmptyStock filter = stockFilter != null ? (FilterEmptyStock) stockFilter.getType() : FilterEmptyStock.HIDDEN;
		FilterEmptyStock next = filter.nextWithLoop();

		ItemBuilder item = new ItemBuilder(Material.BUCKET).name("&6Empty Stock:")
				.lore("&e⬇ " + camelCase(filter.name()))
				.lore("&7⬇ " + camelCase(next.name()));
		contents.set(5, 3, ClickableItem.from(item.build(), e -> {
			formatFilter(stockFilter, next);
			open(player, contents.pagination().getPage());
		}));
	}

	public void addExchangeFilter(Player player, InventoryContents contents) {
		Filter exchangeFilter = getFilter(FilterExchangeType.class);
		FilterExchangeType filter = exchangeFilter != null ? (FilterExchangeType) exchangeFilter.getType() : FilterExchangeType.BOTH;
		FilterExchangeType next = filter.nextWithLoop();

		ItemBuilder item = new ItemBuilder(Material.HOPPER).name("&6Filter by:")
				.lore("&7⬇ " + camelCase(filter.previousWithLoop().name()))
				.lore("&e⬇ " + camelCase(filter.name()))
				.lore("&7⬇ " + camelCase(next.name()));
		contents.set(5, 4, ClickableItem.from(item.build(), e -> {
			formatFilter(exchangeFilter, next);
			open(player, contents.pagination().getPage());
		}));
	}

	public void addMarketFilter(Player player, InventoryContents contents) {
		Filter marketFilter = getFilter(FilterMarketItems.class);
		FilterMarketItems filter = marketFilter != null ? (FilterMarketItems) marketFilter.getType() : FilterMarketItems.SHOWN;
		FilterMarketItems next = filter.nextWithLoop();

		ItemBuilder item = new ItemBuilder(Material.OAK_SIGN).name("&6Market Items:")
				.lore("&e⬇ " + camelCase(filter.name()))
				.lore("&7⬇ " + camelCase(next.name()));
		contents.set(5, 5, ClickableItem.from(item.build(), e -> {
			formatFilter(marketFilter, next);
			open(player, contents.pagination().getPage());
		}));

		if (shopGroup == ShopGroup.SURVIVAL) {
			boolean isResourceWorld = player.getWorld().getName().startsWith("resource");
			Filter world = FilterRequiredType.REQUIRED.of("This worlds items", product -> isResourceWorld == product.isResourceWorld());
			filters.add(world);
		}
	}

	public void addItems(Player player, InventoryContents contents) {
		List<Shop> shops = shop != null ? Collections.singletonList(shop) : service.getShops();
		if (shops == null || shops.size() == 0) return;
		List<ClickableItem> items = new ArrayList<>();

		Pagination page = contents.pagination();

		List<Product> products = new ArrayList<Product>() {{
			shops.forEach(shop -> addAll(shop.getProducts(shopGroup)));
		}}.stream()
				.filter(product -> !isFiltered(product))
				.sorted(Product::compareTo)
				.collect(toList());

		ClickableItem empty = ClickableItem.empty(new ItemStack(Material.BARRIER));

		int perPage = 36;
		if (page.getPage() > products.size() / perPage)
			page.page(products.size() / perPage);

		int start = perPage * page.getPage();
		int end = start + perPage;

		for (int i = 0; i < start; i++)
			items.add(empty);

		products.subList(start, Math.min(end, products.size()))
				.forEach(product -> {
					try {
						items.add(ClickableItem.from(product.getItemWithCustomerLore().build(), e -> {
							if (!product.isPurchasable())
								return;

							try {
								if (handleRightClick(product, e))
									return;

								if (isLeftClick(e))
									product.process(player);
								else if (isShiftLeftClick(e))
									processAll(player, page, product);
								open(player, page);
							} catch (Exception ex) {
								PlayerUtils.send(player, PREFIX + "&c" + ex.getMessage());
							}
						}));
					} catch (Exception ex) {
						Nexus.severe("Error formatting product in BrowseItemsProvider: " + product.toString());
						ex.printStackTrace();
					}
				});

		if (end < products.size())
			items.add(empty);

		addPagination(player, contents, items);
	}

	private void processAll(Player player, Pagination page, Product product) {
		ConfirmationMenu.builder()
				.title("&4" + product.getExchange().getCustomerAction() + " all?")
				.onConfirm(e2 -> {
					try {
						product.processAll(player);
					} catch (Exception ex) {
						PlayerUtils.send(player, PREFIX + "&c" + ex.getMessage());
					}
				})
				.onFinally(e2 -> open(player, page))
				.open(player);
	}

	public boolean isFiltered(Product product) {
		if (!product.isEnabled())
			return true;
		if (!(this instanceof PlayerShopProvider))
			if (!product.isPurchasable())
				return true;

		if (filters != null)
			for (Filter filter : filters)
				if (filter.getFilter() != null)
					if (!filter.getFilter().test(product))
						return true;
		return false;
	}

	private void formatFilter(Filter filter, FilterType next) {
		if (filter != null) {
			filter.setType(next);
			filter.setFilter(next.getFilter());
		} else
			filters.add(new Filter(next, next.getFilter(), null));
	}

	public Filter getFilter(Class<? extends FilterType> type) {
		if (filters != null)
			for (Filter filter : filters)
				if (filter.getType().getClass() == type)
					return filter;
		return null;
	}

	public static class ShulkerContentsProvider extends  _ShopProvider {
		private final Product product;

		public ShulkerContentsProvider(_ShopProvider previousMenu, Product product) {
			this.previousMenu = previousMenu;
			this.product = product;
			this.rows = 4;
		}

		@Override
		public void open(Player viewer, int page) {
			open(viewer, page, this, "&0Shulker Contents");
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			super.init(player, contents);

			contents.set(0, 4, ClickableItem.empty(product.getItemWithCustomerLore().build()));

			int row = 1;
			int column = 0;
			for (ItemStack itemStack : getRawShulkerContents(product.getItem())) {
				contents.set(row, column, ClickableItem.empty(itemStack));

				if (column == 8) {
					column = 0;
					row++;
				} else
					column++;
			}
		}
	}

}
