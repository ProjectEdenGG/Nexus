package me.pugabyte.nexus.features.events.store.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class EventStoreMenu extends MenuUtils implements InventoryProvider {

	abstract protected EventStoreMenu getPreviousMenu();

	@Override
	public void init(Player player, InventoryContents contents) {
		if (getPreviousMenu() == null)
			addCloseItem(contents);
		else
			addBackItem(contents, e -> getPreviousMenu().open(player));

		ItemStack tokens = new ItemBuilder(Material.BOOK).name("Tokens").build();
		contents.set(0, 8, ClickableItem.empty(tokens));
	}

}
