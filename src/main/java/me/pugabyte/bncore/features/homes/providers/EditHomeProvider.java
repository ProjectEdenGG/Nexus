package me.pugabyte.bncore.features.homes.providers;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.homes.HomesMenu;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.homes.Home;
import me.pugabyte.bncore.models.homes.HomeOwner;
import org.bukkit.entity.Player;

public class EditHomeProvider extends MenuUtils implements InventoryProvider {
	private Home home;
	private HomeOwner homeOwner;

	public EditHomeProvider(Home home) {
		this.home = home;
		this.homeOwner = home.getOwner();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> HomesMenu.edit(homeOwner));

		// TODO: Lock/unlock
		// TODO: Set display item
		// TODO: Rename
		// TODO: Teleport
		// TODO: Set to current position
		// TODO: Delete
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}

}
