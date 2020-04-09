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

		contents.set(1, 3, ClickableItem.from(nameItem(Material.CHEST, "Player shops"), e -> ShopMenu.PLAYER_SHOPS.open(player, this)));
	}


}
