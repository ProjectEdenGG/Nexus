package me.pugabyte.bncore.features.menus.itemeditor;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.features.menus.itemeditor.providers.ItemEditorProvider;
import org.bukkit.entity.Player;

public class ItemEditorMenu {
	public static void openItemEditor(Player player, ItemEditMenu menu) {
		SmartInventory inv = SmartInventory.builder()
				.provider(new ItemEditorProvider(menu))
				.size(menu.getSize(), 9)
				.title("Customize Item")
				.build();
		inv.open(player);
	}

}
