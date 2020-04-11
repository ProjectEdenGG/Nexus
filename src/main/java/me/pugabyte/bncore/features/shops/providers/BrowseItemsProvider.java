package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import me.pugabyte.bncore.features.shops.ShopMenu;
import me.pugabyte.bncore.features.shops.ShopMenu.Filter;
import me.pugabyte.bncore.features.shops.ShopMenu.FilterEmptyStock;
import me.pugabyte.bncore.features.shops.ShopMenu.FilterExchangeType;
import me.pugabyte.bncore.features.shops.ShopMenu.FilterMarketItems;
import me.pugabyte.bncore.features.shops.ShopMenu.FilterSearchType;
import me.pugabyte.bncore.features.shops.ShopMenu.FilterType;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class BrowseItemsProvider extends _ShopProvider {
	private List<Filter> filters;

	public BrowseItemsProvider(_ShopProvider previousMenu) {
		this(previousMenu, new ArrayList<>());
	}

	public BrowseItemsProvider(_ShopProvider previousMenu, List<Filter> filters) {
		this.previousMenu = previousMenu;
		if (previousMenu != null && previousMenu.getPreviousMenu() != null && previousMenu.getPreviousMenu().getPreviousMenu() != null
				&& previousMenu.getPreviousMenu().getClass() == this.getClass())
			this.previousMenu = previousMenu.getPreviousMenu().getPreviousMenu();
		this.filters = new ArrayList<>(filters);
		addDefaultFilters();
	}

	private void addDefaultFilters() {
		if (getFilter(FilterEmptyStock.class) == null)
			filters.add(FilterEmptyStock.HIDDEN.get());
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.title(colorize("&0Browse Items"))
				.size(6, 9)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		addFilters(player, contents);
		addItems(player, contents);
	}

	public void addFilters(Player player, InventoryContents contents) {

		Pagination page = contents.pagination();

		{
			Filter searchFilter = getFilter(FilterSearchType.class);
			if (searchFilter != null) {
				ItemStack search = new ItemBuilder(Material.COMPASS).name("&6Current filter: &e" + searchFilter.getMessage())
						.lore("").lore("&7Click to remove filter").glow().build();
				contents.set(0, 2, ClickableItem.from(search, e -> {
					filters.remove(searchFilter);
					open(player, page.getPage());
				}));
			} else
				contents.set(0, 2, ClickableItem.from(nameItem(Material.COMPASS, "&6Filter Items"), e -> ShopMenu.SEARCH_ITEMS.open(player, this)));
		}

		{
			Filter exchangeFilter = getFilter(FilterExchangeType.class);
			FilterExchangeType filter = exchangeFilter != null ? (FilterExchangeType) exchangeFilter.getType() : FilterExchangeType.BOTH;
			FilterExchangeType next = filter.nextWithLoop();

			ItemBuilder item = new ItemBuilder(Material.HOPPER).name("&6Filter by:")
					.lore("&7" + camelCase(filter.previousWithLoop().name()))
					.lore("&e" + camelCase(filter.name()))
					.lore("&7" + camelCase(next.name()));
			contents.set(5, 3, ClickableItem.from(item.build(), e -> {
				formatFilter(exchangeFilter, next);
				open(player, page.getPage());
			}));
		}

		{
			Filter marketFilter = getFilter(FilterMarketItems.class);
			FilterMarketItems filter = marketFilter != null ? (FilterMarketItems) marketFilter.getType() : FilterMarketItems.SHOWN;
			FilterMarketItems next = filter.nextWithLoop();

			ItemBuilder item = new ItemBuilder(Material.OAK_SIGN).name("&6Market Items:")
					.lore("&e" + camelCase(filter.name()))
					.lore("&7" + camelCase(next.name()));
			contents.set(5, 4, ClickableItem.from(item.build(), e -> {
				formatFilter(marketFilter, next);
				open(player, page.getPage());
			}));
		}

		{
			Filter stockFilter = getFilter(FilterEmptyStock.class);
			FilterEmptyStock filter = stockFilter != null ? (FilterEmptyStock) stockFilter.getType() : FilterEmptyStock.HIDDEN;
			FilterEmptyStock next = filter.nextWithLoop();

			ItemBuilder item = new ItemBuilder(Material.BUCKET).name("&6Empty Stock:")
					.lore("&e" + camelCase(filter.name()))
					.lore("&7" + camelCase(next.name()));
			contents.set(5, 5, ClickableItem.from(item.build(), e -> {
				formatFilter(stockFilter, next);
				open(player, page.getPage());
			}));
		}
	}

	public void addItems(Player player, InventoryContents contents) {
		List<Shop> shops = service.getShops();
		if (shops == null || shops.size() == 0) return;
		List<ClickableItem> items = new ArrayList<>();

		Pagination page = contents.pagination();

		service.getShops().forEach(shop -> shop.getProducts().forEach(product -> {
			if (filters != null)
				for (Filter filter : filters)
					if (filter.getFilter() != null)
						if (!filter.getFilter().apply(product)) return;

			ItemStack item  = new ItemBuilder(product.getItem())
					.lore(product.getExchange().getLore(product))
					.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
					.build();

			items.add(ClickableItem.from(item, e -> {
				try {
					product.getExchange().process(product, player);
					open(player, page.getPage());
				} catch (Exception ex) {
					player.sendMessage(colorize(ex.getMessage()));
				}
			}));
		}));

		addPagination(player, contents, items);
	}

	private void formatFilter(Filter filter, FilterType next) {
		if (filter != null) {
			filter.setType(next);
			filter.setFilter(next.getFilter());
		} else
			filters.add(new Filter(next, next.getFilter(), null));
	}

	private Filter getFilter(Class<? extends FilterType> type) {
		if (filters != null)
			for (Filter filter : filters)
				if (filter.getType().getClass() == type)
					return filter;
		return null;
	}

}
