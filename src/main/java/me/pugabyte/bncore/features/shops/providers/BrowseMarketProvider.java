package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.entity.Player;

public class BrowseMarketProvider extends _ShopProvider {

	public BrowseMarketProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		open(viewer, page, this, "&0Browse Market");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

	}


}
