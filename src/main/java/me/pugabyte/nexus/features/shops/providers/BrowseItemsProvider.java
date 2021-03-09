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
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.FilterSearchType;
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.FilterType;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.Product;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.nexus.features.shops.Shops.PREFIX;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;

public class BrowseItemsProvider extends _ShopProvider {
	@Getter
	protected List<Filter> filters;
	protected Shop shop;

	public BrowseItemsProvider(_ShopProvider previousMenu) {
		this(previousMenu, null, new ArrayList<>());
	}

	public BrowseItemsProvider(_ShopProvider previousMenu, Shop shop) {
		this(previousMenu, shop, new ArrayList<>());
	}

	public BrowseItemsProvider(_ShopProvider previousMenu, Filter filter) {
		this(previousMenu, null, Collections.singletonList(filter));
	}

	public BrowseItemsProvider(_ShopProvider previousMenu, Shop shop, Filter filter) {
		this(previousMenu, shop, Collections.singletonList(filter));
	}

	public BrowseItemsProvider(_ShopProvider previousMenu, Shop shop, List<Filter> filters) {
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
			contents.set(0, 4, ClickableItem.from(nameItem(Material.COMPASS, "&6Filter Items"), e -> new SearchItemsProvider(this).open(player)));
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
	}

	public void addItems(Player player, InventoryContents contents) {
		List<Shop> shops = shop != null ? Collections.singletonList(shop) : service.getShops();
		if (shops == null || shops.size() == 0) return;
		List<ClickableItem> items = new ArrayList<>();

		Pagination page = contents.pagination();

		shops.forEach(shop -> shop.getProducts(ShopGroup.get(player)).forEach(product -> {
			try {
				if (isFiltered(product))
					return;

				ItemBuilder builder  = new ItemBuilder(product.getItem());

				ItemMeta meta = product.getItem().getItemMeta();
				if (meta.hasLore())
					builder.lore(product.getItem().getLore());
				if (meta.hasLore() || meta.hasEnchants())
					builder.lore("&f");

				builder.lore(product.getExchange().getLore(product))
						.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
						.build();

				items.add(ClickableItem.from(builder.build(), e -> {
					try {
						product.process(player);
						open(player, page.getPage());
					} catch (Exception ex) {
						PlayerUtils.send(player, PREFIX + ex.getMessage());
					}
				}));
			} catch (Exception ex) {
				Nexus.severe("Error formatting product in BrowseItemsProvider: " + StringUtils.toPrettyString(product));
				ex.printStackTrace();
			}
		}));

		addPagination(player, contents, items);
	}

	public boolean isFiltered(Product product) {
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

}
