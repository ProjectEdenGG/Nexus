package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.bncore.models.shop.Shop;
import org.bukkit.entity.Player;

public class PlayerShopProvider extends BrowseItemsProvider {

	public PlayerShopProvider(_ShopProvider previousMenu, Shop shop) {
		super(previousMenu, shop);
	}

	@Override
	public void open(Player viewer, int page) {
		open(viewer, page, this, "&0" + shop.getOfflinePlayer().getName() + "'s shop");
	}

	@Override
	public void addFilters(Player player, InventoryContents contents) {
		addSearchFilter(player, contents);
		addStockFilter(player, contents);
		addExchangeFilter(player, contents);
	}

}
