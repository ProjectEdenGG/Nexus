package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.bncore.models.shop.ShopService;
import org.bukkit.entity.Player;

public class BrowseMarketProvider extends PlayerShopProvider {

	public BrowseMarketProvider(_ShopProvider previousMenu) {
		super(previousMenu, new ShopService().getMarket());
	}

	@Override
	public void open(Player viewer, int page) {
		open(viewer, page, this, "&0Browse Market");
	}

	@Override
	public void addFilters(Player player, InventoryContents contents) {
		addSearchFilter(player, contents);
		addExchangeFilter(player, contents);
	}

}
