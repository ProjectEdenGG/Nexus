package gg.projecteden.nexus.features.resourcepack.decoration.catalog;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Theme;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;

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

		List<ClickableItem> items = new ArrayList<>();

		for (Theme theme : Theme.values()) {
			if (theme == Theme.ALL)
				continue;

			ItemBuilder catalogTheme = theme.getItemBuilder().name(StringUtils.camelCase(theme));

			items.add(ClickableItem.of(catalogTheme, e -> Catalog.openCatalog(e.getPlayer(), theme, this)));
		}

		paginator().items(items).useGUIArrows().build();
	}
}
