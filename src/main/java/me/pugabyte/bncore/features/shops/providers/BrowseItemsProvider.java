package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.Shop.Product;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class BrowseItemsProvider extends _ShopProvider {
	List<Function<Product, Boolean>> filters;

	public BrowseItemsProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	public BrowseItemsProvider(_ShopProvider previousMenu, List<Function<Product, Boolean>> filters) {
		this.previousMenu = previousMenu;
		this.filters = filters;
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
		List<Shop> shops = service.getShops();
		if (shops == null || shops.size() == 0) return;
		List<ClickableItem> items = new ArrayList<>();

		Pagination page = contents.pagination();

		service.getShops().forEach(shop -> shop.getProducts().forEach(product -> {
			if (filters != null)
				for (Function<Product, Boolean> filter : filters)
					if (!filter.apply(product)) return;

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

}
