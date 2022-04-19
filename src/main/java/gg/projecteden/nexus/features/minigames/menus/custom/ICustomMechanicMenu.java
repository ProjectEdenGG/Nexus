package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import org.bukkit.entity.Player;

public abstract class ICustomMechanicMenu extends InventoryProvider {

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
			.provider(this)
			.title("Custom Settings Menu")
			.rows(3)
			.build()
			.open(player, page);
	}

}
