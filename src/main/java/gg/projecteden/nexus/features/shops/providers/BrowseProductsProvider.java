package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SignMenuFactory;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.features.shops.Shops;
import gg.projecteden.nexus.features.shops.providers.common.ShopMenuFunctions.Filter;
import gg.projecteden.nexus.features.shops.providers.common.ShopMenuFunctions.FilterEmptyStock;
import gg.projecteden.nexus.features.shops.providers.common.ShopMenuFunctions.FilterExchangeType;
import gg.projecteden.nexus.features.shops.providers.common.ShopMenuFunctions.FilterMarketItems;
import gg.projecteden.nexus.features.shops.providers.common.ShopMenuFunctions.FilterRequiredType;
import gg.projecteden.nexus.features.shops.providers.common.ShopMenuFunctions.FilterSearchType;
import gg.projecteden.nexus.features.shops.providers.common.ShopMenuFunctions.FilterType;
import gg.projecteden.nexus.features.shops.providers.common.ShopMenuFunctions.Sorter;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Title("&0Browse Items")
public class BrowseProductsProvider extends ShopProvider {
	@Getter
	protected List<Filter> filters;
	protected Sorter sorter = Sorter.DEFAULT;
	protected Shop shop;

	public BrowseProductsProvider(ShopProvider previousMenu) {
		this(previousMenu, null, new ArrayList<>());
	}

	public BrowseProductsProvider(ShopProvider previousMenu, Shop shop) {
		this(previousMenu, shop, new ArrayList<>());
	}

	public BrowseProductsProvider(ShopProvider previousMenu, Filter filter) {
		this(previousMenu, null, Collections.singletonList(filter));
	}

	public BrowseProductsProvider(ShopProvider previousMenu, Shop shop, Filter filter) {
		this(previousMenu, shop, Collections.singletonList(filter));
	}

	public BrowseProductsProvider(ShopProvider previousMenu, Shop shop, List<Filter> filters) {
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
	public void init() {
		super.init();

		filters.add(FilterRequiredType.REQUIRED.of("No resource world items", product -> !product.isResourceWorld()));
		addSorter(contents);
		addFilters(contents);
		addItems(contents);
	}

	public void addSorter(InventoryContents contents) {
		Sorter sorter = this.sorter != null ? this.sorter : Sorter.DEFAULT;
		Sorter next = sorter.nextWithLoop();
		Sorter next2 = next.nextWithLoop();

		var item = new ItemBuilder(Material.MAGENTA_GLAZED_TERRACOTTA).name("&6Sort by:")
			.lore("&e⬇ " + StringUtils.camelCase(sorter.name()))
			.lore("&7⬇ " + StringUtils.camelCase(next.name()))
			.lore("&7⬇ " + StringUtils.camelCase(next2.name()));

		contents.set(5, 2, ClickableItem.of(item.build(), e -> {
			this.sorter = next;
			refresh();
		}));
	}

	public void addFilters(InventoryContents contents) {
		addSearchFilter(contents);
		addStockFilter(contents);
		addExchangeFilter(contents);
		addMarketFilter(contents);
	}

	public void addSearchFilter(InventoryContents contents) {
		Filter searchFilter = getFilter(FilterSearchType.class);
		if (searchFilter != null) {
			ItemStack search = new ItemBuilder(Material.COMPASS)
				.name("&6Current filter: &e" + searchFilter.getMessage())
				.lore("").lore("&7Click to remove filter")
				.glow()
				.build();

			contents.set(0, 4, ClickableItem.of(search, e -> {
				filters.remove(searchFilter);
				refresh();
			}));
		} else
			contents.set(0, 4, ClickableItem.of(Material.COMPASS, "&6Filter Items", e -> new SearchProductsProvider(this).open(viewer)));
	}

	public void addStockFilter(InventoryContents contents) {
		Filter stockFilter = getFilter(FilterEmptyStock.class);
		FilterEmptyStock filter = stockFilter != null ? (FilterEmptyStock) stockFilter.getType() : FilterEmptyStock.HIDDEN;
		FilterEmptyStock next = filter.nextWithLoop();

		ItemBuilder item = new ItemBuilder(Material.BUCKET).name("&6Empty Stock:")
			.lore("&e⬇ " + StringUtils.camelCase(filter.name()))
			.lore("&7⬇ " + StringUtils.camelCase(next.name()));

		contents.set(5, 3, ClickableItem.of(item.build(), e -> {
			formatFilter(stockFilter, next);
			refresh();
		}));
	}

	public void addExchangeFilter(InventoryContents contents) {
		Filter exchangeFilter = getFilter(FilterExchangeType.class);
		FilterExchangeType filter = exchangeFilter != null ? (FilterExchangeType) exchangeFilter.getType() : FilterExchangeType.BOTH;
		FilterExchangeType next = filter.nextWithLoop();

		ItemBuilder item = new ItemBuilder(Material.HOPPER).name("&6Filter by:")
			.lore("&7⬇ " + StringUtils.camelCase(filter.previousWithLoop().name()))
			.lore("&e⬇ " + StringUtils.camelCase(filter.name()))
			.lore("&7⬇ " + StringUtils.camelCase(next.name()));

		contents.set(5, 4, ClickableItem.of(item.build(), e -> {
			formatFilter(exchangeFilter, next);
			refresh();
		}));
	}

	public void addMarketFilter(InventoryContents contents) {
		Filter marketFilter = getFilter(FilterMarketItems.class);
		FilterMarketItems filter = marketFilter != null ? (FilterMarketItems) marketFilter.getType() : FilterMarketItems.SHOWN;
		FilterMarketItems next = filter.nextWithLoop();

		ItemBuilder item = new ItemBuilder(Material.OAK_SIGN).name("&6Market Items:")
			.lore("&e⬇ " + StringUtils.camelCase(filter.name()))
			.lore("&7⬇ " + StringUtils.camelCase(next.name()));

		contents.set(5, 5, ClickableItem.of(item.build(), e -> {
			formatFilter(marketFilter, next);
			refresh();
		}));
	}

	public void addItems(InventoryContents contents) {
		List<Shop> shops = shop != null ? Collections.singletonList(shop) : service.getShops();
		if (shops == null || shops.size() == 0) return;
		List<ClickableItem> items = new ArrayList<>();

		Pagination page = contents.pagination();

		List<Product> products = new ArrayList<Product>() {{
			shops.forEach(shop -> addAll(shop.getProducts(shopGroup)));
		}}.stream()
			.filter(product -> !isFiltered(product))
			.sorted(sorter.getComparator())
			.toList();

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
					items.add(ClickableItem.of(product.getItemWithCustomerLore(), e -> {
						if (!product.isPurchasable())
							return;

						try {
							if (handleRightClick(product, e))
								return;

							if (e.isLeftClick()) {
								if (product.getUuid().equals(viewer.getUniqueId()))
									new EditProductProvider(this, product).open(viewer);
								else {
									product.process(viewer);
									refresh();
								}
							} else if (e.isShiftLeftClick())
								Nexus.getSignMenuFactory()
									.lines("", SignMenuFactory.ARROWS, "Enter amount to", product.getExchange().getCustomerAction().toLowerCase() + " or 'all'")
									.prefix(Shops.PREFIX)
									.onError(this::refresh)
									.response(lines -> {
										if (lines[0].length() > 0) {
											String input = lines[0];
											if ("all".equalsIgnoreCase(input))
												processAll(product);
											else if (!Utils.isInt(input))
												throw new InvalidInputException("Could not parse &e" + input + " &cas a number");
											else
												process(product, Integer.parseInt(input));
										}
										refresh();
									})
									.open(viewer);
						} catch (Exception ex) {
							PlayerUtils.send(viewer, Shops.PREFIX + "&c" + ex.getMessage());
						}
					}));
				} catch (Exception ex) {
					Nexus.severe("Error formatting product in BrowseItemsProvider: " + product.toString());
					ex.printStackTrace();
				}
			});

		if (end < products.size())
			items.add(empty);

		paginate(items);
	}

	private void process(Product product, int amount) {
		try {
			if (amount < product.getItem().getAmount())
				throw new InvalidInputException("Amount cannot be less than product amount");

			product.processMany(viewer, amount / product.getItem().getAmount());
		} catch (Exception ex) {
			MenuUtils.handleException(viewer, Shops.PREFIX, ex);
		} finally {
			refresh();
		}
	}

	private void processAll(Product product) {
		try {
			product.processAll(viewer);
		} catch (Exception ex) {
			MenuUtils.handleException(viewer, Shops.PREFIX, ex);
		} finally {
			refresh();
		}
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

	@Rows(4)
	@Title("&0Shulker Contents")
	public static class ShulkerContentsProvider extends ShopProvider {
		private final Product product;

		public ShulkerContentsProvider(ShopProvider previousMenu, Product product) {
			this.previousMenu = previousMenu;
			this.product = product;
		}

		@Override
		public void init() {
			super.init();

			contents.set(0, 4, ClickableItem.empty(product.getItemWithCustomerLore().build()));

			int row = 1;
			int column = 0;
			for (ItemStack itemStack : ItemUtils.getRawShulkerContents(product.getItem())) {
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
