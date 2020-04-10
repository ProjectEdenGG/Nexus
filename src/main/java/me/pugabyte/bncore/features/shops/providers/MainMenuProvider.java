package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.bncore.features.shops.ShopMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class MainMenuProvider extends _ShopProvider {

	public MainMenuProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.title(colorize("&0Shops"))
				.size(6, 9)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		contents.set(1, 2, ClickableItem.from(nameItem(Material.CHEST, "&6&lBrowse Market"), e -> ShopMenu.BROWSE_MARKET.open(player, this)));
		contents.set(1, 4, ClickableItem.from(nameItem(Material.CHEST, "&6&lBrowse Shops"), e -> ShopMenu.BROWSE_SHOPS.open(player, this)));
		contents.set(1, 6, ClickableItem.from(nameItem(Material.CHEST, "&6&lBrowse Items"), e -> ShopMenu.BROWSE_ITEMS.open(player, this)));

		contents.set(3, 2, ClickableItem.from(nameItem(Material.COMPASS, "&6&lSearch items"), e -> ShopMenu.BROWSE_ITEMS.open(player, this)));
		contents.set(3, 4, ClickableItem.empty(nameItem(Material.HOPPER, "&6&lView Categories", "&eComing soon™")));
		contents.set(3, 6, ClickableItem.from(nameItem(Material.PLAYER_HEAD, "&6&lYour Shop"), e -> ShopMenu.YOUR_SHOP.open(player, this)));
	}


}
