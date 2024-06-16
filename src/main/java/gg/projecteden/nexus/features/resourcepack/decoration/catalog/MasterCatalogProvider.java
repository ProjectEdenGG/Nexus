package gg.projecteden.nexus.features.resourcepack.decoration.catalog;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.decoration.DecorationUser;
import gg.projecteden.nexus.models.decoration.DecorationUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Title("Catalog | Theme Picker")
public class MasterCatalogProvider extends InventoryProvider {
	private final DecorationUser user;

	public MasterCatalogProvider(Player player) {
		user = new DecorationUserService().get(player);
	}

	@Override
	public void init() {
		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();
		for (Catalog.Theme theme : user.getOwnedThemes().stream().sorted().toList()) {
			ItemBuilder catalogTheme = theme.getItemBuilder().name("&3" + StringUtils.camelCase(theme));
			items.add(ClickableItem.of(catalogTheme, e -> Catalog.openCatalog(e.getPlayer(), theme, this)));
		}
		paginator().items(items).useGUIArrows().build();
	}
}
