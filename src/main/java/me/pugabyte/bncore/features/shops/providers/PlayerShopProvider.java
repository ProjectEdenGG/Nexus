package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.Shop.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class PlayerShopProvider extends _ShopProvider {
	private Shop shop;

	public PlayerShopProvider(Shop shop, _ShopProvider previousMenu) {
		this.shop = shop;
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer) {
		SmartInventory.builder()
				.provider(this)
				.title(colorize("&0" + shop.getOfflinePlayer().getName() + "'s shop"))
				.size(6, 9)
				.build()
				.open(viewer);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		int index = 9;
		for (ShopItem shopItem : shop.getItems()) {
			ItemStack item = shopItem.getItem().clone();
			// lore and stuff
			contents.set(index++, ClickableItem.from(item, e -> {
				BNCore.log("Class of exchange: " + shopItem.getExchange().getClass().getSimpleName());
				shopItem.getExchange().process(shopItem, player);
			}));
		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {}

}
