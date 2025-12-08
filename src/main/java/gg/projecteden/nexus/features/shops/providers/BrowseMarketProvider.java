package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.shops.Market;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import org.bukkit.entity.Player;

@Title("&0Browse Market")
public class BrowseMarketProvider extends PlayerShopProvider {

	public BrowseMarketProvider(ShopProvider previousMenu) {
		super(previousMenu, new ShopService().getMarket());
	}

	@Override
	public void open(Player viewer, int page) {
		if (SubWorldGroup.of(viewer.getWorld()) == SubWorldGroup.RESOURCE) {
			new ResourceWorldMarketProvider(previousMenu).open(viewer);
			return;
		}

		if (shop.getProducts().isEmpty())
			Market.load();

		super.open(viewer, page);
	}

	@Override
	public void addFilters(InventoryContents contents) {
		addSearchFilter(contents);
		addExchangeFilter(contents);
	}

}
