package me.pugabyte.bncore.features.holidays.pugmas20.menu.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.holidays.pugmas20.Pugmas20;
import me.pugabyte.bncore.features.menus.MenuUtils;
import org.bukkit.entity.Player;

public class AdventProvider extends MenuUtils implements InventoryProvider {
	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents, 5, 1);
		Pugmas20.getAdventHeadMap().forEach((slotPos, skull) -> {
			contents.set(slotPos, ClickableItem.empty(skull.build()));
		});
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
