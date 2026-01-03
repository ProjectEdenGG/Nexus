package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.badge.BadgeUser;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class BadgeViewerProvider extends InventoryProvider {
	private final InventoryProvider previousMenu;
	private final BadgeUser badgeUser;

	public BadgeViewerProvider(InventoryProvider previousMenu, BadgeUser badgeUser) {
		this.previousMenu = previousMenu;
		this.badgeUser = badgeUser;
	}

	@Override
	public String getTitle() {
		return blankTexture() + "&8All Badges:";
	}

	@Override
	public void init() {
		addBackOrCloseItem(previousMenu);

		List<ClickableItem> items = new ArrayList<>();
		for (Badge badge : Badge.values()) {
			if (badge.getHowToObtain() == null)
				continue;

			if (badge.getModelType() == ItemModelType.NULL)
				continue;

			ItemBuilder item = new ItemBuilder(badge.getModelType())
				.name(badge.getName() + " Badge")
				.lore(badge.getLore(badgeUser))
				.lore("")
				.lore("&3How To Obtain:")
				.lore("&e" + badge.getHowToObtain());

			items.add(ClickableItem.empty(item));
		}

		paginate(items);
	}
}
