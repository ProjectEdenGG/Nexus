package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.models.shop.Shop;

public class PlayerShopProvider extends BrowseProductsProvider {

	public PlayerShopProvider(ShopProvider previousMenu, Shop shop) {
		super(previousMenu, shop);
	}

	@Override
	public String getTitle() {
		return "&0" + shop.getNickname() + "'s shop";
	}

	@Override
	public void addFilters(InventoryContents contents) {
		addSearchFilter(contents);
		addStockFilter(contents);
		addExchangeFilter(contents);
	}

}
