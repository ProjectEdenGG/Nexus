package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.bncore.models.shop.Shop.Product;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class EditProductProvider extends _ShopProvider {
	private Product product;

	public EditProductProvider(_ShopProvider previousMenu, Product product) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.title(colorize("&0Edit Item"))
				.size(6, 9)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

	}

}
