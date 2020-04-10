package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class YourShopProvider extends _ShopProvider {

	public YourShopProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.title(colorize("&0Your shop"))
				.size(6, 9)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

	}


}
