package gg.projecteden.nexus.features.menus.itemeditor;

import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.itemeditor.providers.ItemEditorProvider;
import org.bukkit.entity.Player;

public class ItemEditorMenu {

	public static void openItemEditor(Player player, ItemEditMenu menu) {
		SmartInventory inv = SmartInventory.builder()
				.provider(new ItemEditorProvider(menu))
				.rows(menu.getSize())
				.title("Customize Item")
				.build();
		inv.open(player);
	}

}
