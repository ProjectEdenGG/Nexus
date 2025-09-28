package gg.projecteden.nexus.features.resourcepack.decoration.catalog;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Theme;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreCurrencyType;
import gg.projecteden.nexus.models.decoration.DecorationUser;
import gg.projecteden.nexus.models.decoration.DecorationUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Title("Catalog | Theme Picker")
public class MasterCatalogProvider extends InventoryProvider {
	private final DecorationUser user;
	DecorationStoreCurrencyType currency;

	public MasterCatalogProvider(Player player, DecorationStoreCurrencyType currency) {
		this.user = new DecorationUserService().get(player);
		this.currency = currency;
	}

	@Override
	public void init() {
		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();
		List<Catalog.Theme> ownedThemes = user.getOwnedThemes();
		if (WorldGroup.of(user).isCreativeMode())
			ownedThemes = new ArrayList<>(List.of(Theme.values()));

		ownedThemes.remove(Theme.MASTER);

		for (Catalog.Theme theme : ownedThemes.stream().sorted().toList()) {
			ItemBuilder catalogTheme = theme.getItemBuilder().name("&3" + StringUtils.camelCase(theme));
			items.add(ClickableItem.of(catalogTheme, e -> Catalog.openCatalog(e.getPlayer(), theme, currency, this)));
		}
		paginator().items(items).useGUIArrows().build();
	}
}
