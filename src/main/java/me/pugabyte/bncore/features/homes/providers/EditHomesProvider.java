package me.pugabyte.bncore.features.homes.providers;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.homes.HomeOwner;
import org.bukkit.entity.Player;

public class EditHomesProvider extends MenuUtils implements InventoryProvider {
	private HomeOwner homeOwner;

	public EditHomesProvider(HomeOwner homeOwner) {
		this.homeOwner = homeOwner;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents);

		// TODO: Set new home
		// TODO: Auto lock
		// TODO: Allow all
		// TODO: Remove all
		// TODO: Lock all homes
		// TODO: Unlock all homes

		// TODO: Format homes

	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}

}
