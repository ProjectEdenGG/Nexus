package me.pugabyte.bncore.features.recipes;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CraftingRecipeProvider extends MenuUtils implements InventoryProvider {

	CraftingRecipeMenu menu;

	public CraftingRecipeProvider(CraftingRecipeMenu menu) {
		this.menu = menu;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		switch (menu) {
			case MAIN:
				addCloseItem(contents);
				break;
			default:
				contents.fillRect(0, 0, 2, 1, ClickableItem.empty(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name("").build()));
				contents.fillRect(0, 5, 2, 8, ClickableItem.empty(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name("").build()));
				contents.set(1, 6, ClickableItem.NONE);
				addBackItem(contents, e -> CustomRecipesCommand.openMenu(CraftingRecipeMenu.MAIN, player));
		}
	}

	public static int ticks = 1;
	public static int index = 0;

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
		if (menu == CraftingRecipeMenu.MAIN) return;
		if (ticks == 20) {

		}
	}
}
