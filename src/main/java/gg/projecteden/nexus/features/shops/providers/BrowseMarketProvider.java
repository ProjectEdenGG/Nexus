package gg.projecteden.nexus.features.shops.providers;

import fr.minuskube.inv.content.InventoryContents;
import gg.projecteden.nexus.features.shops.ShopMenuFunctions.FilterRequiredType;
import gg.projecteden.nexus.features.shops.Shops.Market;
import gg.projecteden.nexus.models.shop.ShopService;
import org.bukkit.entity.Player;

public class BrowseMarketProvider extends PlayerShopProvider {

	public BrowseMarketProvider(_ShopProvider previousMenu) {
		super(previousMenu, new ShopService().getMarket());
	}

	@Override
	public void open(Player player, int page) {
		if (shop.getProducts().isEmpty())
			Market.load();

		open(player, page, this, "&0Browse Market");
	}

	@Override
	public void addFilters(Player player, InventoryContents contents) {
		addSearchFilter(player, contents);
		addExchangeFilter(player, contents);

		boolean isResourceWorld = player.getWorld().getName().startsWith("resource");
		filters.add(FilterRequiredType.REQUIRED.of("This worlds items", product -> isResourceWorld == product.isResourceWorld()));
	}

}
