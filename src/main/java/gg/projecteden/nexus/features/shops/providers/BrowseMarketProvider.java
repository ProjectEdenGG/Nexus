package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.shops.Market;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.models.shop.ShopService;
import org.bukkit.entity.Player;

import static gg.projecteden.nexus.utils.WorldGroup.isResourceWorld;

@Title("&0Browse Market")
public class BrowseMarketProvider extends PlayerShopProvider {

	public BrowseMarketProvider(ShopProvider previousMenu) {
		super(previousMenu, new ShopService().getMarket());
	}

	@Override
	public void open(Player player, int page) {
		if (isResourceWorld(player.getWorld())) {
			new ResourceWorldMarketProvider(previousMenu).open(player);
			return;
		}

		if (shop.getProducts().isEmpty())
			Market.load();

		super.open(player, page);
	}

	@Override
	public void addFilters(Player player, InventoryContents contents) {
		addSearchFilter(player, contents);
		addExchangeFilter(player, contents);
	}

}
