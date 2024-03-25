package gg.projecteden.nexus.features.resourcepack.decoration.catalog;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Theme;
import gg.projecteden.nexus.features.workbenches.dyestation.CreativeBrushMenu;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Title("Catalog | Theme Picker")
public class CatalogProvider extends InventoryProvider {
	InventoryProvider previousMenu;

	public CatalogProvider(InventoryProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void init() {
		addBackOrCloseItem(previousMenu);

		addInfoItems(contents, viewer);

		List<ClickableItem> items = new ArrayList<>();

		for (Theme theme : Theme.values()) {
			if (theme == Theme.ALL)
				continue;

			ItemBuilder catalogTheme = theme.getItemBuilder().name(StringUtils.camelCase(theme));

			items.add(ClickableItem.of(catalogTheme, e -> Catalog.openCatalog(e.getPlayer(), theme, this)));
		}

		paginator().items(items).useGUIArrows().build();
	}

	private static final ItemStack INFO_SURVIVAL = new ItemBuilder(Material.BOOK).name("&6Unsure about a decoration?")
			.lore("&3Use &c/decor catalog &3in the creative world to get access to this menu and try them out!")
			.loreize(true)
			.build();

	private static final ItemStack INFO_CREATIVE = new ItemBuilder(Material.BOOK).name("&6Want access to this menu in Survival?")
			.lore("&3Visit &c/decor store&3, and the merchant to buy these catalogs!")
			.loreize(true)
			.build();

	public static void addInfoItems(InventoryContents _contents, Player viewer) {
		WorldGroup worldGroup = WorldGroup.of(viewer);
		if (worldGroup == WorldGroup.CREATIVE) {
			ItemBuilder creativeBrush = CreativeBrushMenu.getCreativeBrush();

			_contents.set(SlotPos.of(0, 8), ClickableItem.empty(INFO_CREATIVE));
			_contents.set(SlotPos.of(5, 4), ClickableItem.of(creativeBrush, e -> PlayerUtils.giveItem(viewer, creativeBrush.get())));
			return;
		}

		_contents.set(SlotPos.of(0, 8), ClickableItem.empty(INFO_SURVIVAL));
	}
}
