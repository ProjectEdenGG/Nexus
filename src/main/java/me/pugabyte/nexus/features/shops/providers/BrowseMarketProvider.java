package me.pugabyte.nexus.features.shops.providers;

import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.nexus.features.shops.Market;
import me.pugabyte.nexus.features.shops.providers.common.ShopProvider;
import me.pugabyte.nexus.models.shop.ShopService;
import org.bukkit.entity.Player;

import static me.pugabyte.nexus.utils.WorldGroup.isResourceWorld;

public class BrowseMarketProvider extends PlayerShopProvider {

	public BrowseMarketProvider(ShopProvider previousMenu) {
		super(previousMenu, new ShopService().getMarket());
	}

	@Override
	public void open(Player player, int page) {
		if (isResourceWorld(player.getWorld())) {
			new ResourceMarketProvider(previousMenu).open(player);
			return;
		}

		if (shop.getProducts().isEmpty())
			Market.load();

		open(player, page, this, "&0Browse Market");
	}

	@Override
	public void addFilters(Player player, InventoryContents contents) {
		addSearchFilter(player, contents);
		addExchangeFilter(player, contents);
	}

}
