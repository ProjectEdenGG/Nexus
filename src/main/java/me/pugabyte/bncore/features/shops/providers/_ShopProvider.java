package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import org.bukkit.entity.Player;

public abstract class _ShopProvider extends MenuUtils implements InventoryProvider {
	_ShopProvider previousMenu;

	public void open(Player viewer) {
		open(viewer, 0);
	}

	abstract public void open(Player viewer, int page);

	@Override
	public void init(Player player, InventoryContents contents) {
		if (previousMenu == null)
			addCloseItem(contents);
		else
			addBackItem(contents, e -> previousMenu.open(player));
	}

	@Override
	public void update(Player player, InventoryContents contents) {}

}
