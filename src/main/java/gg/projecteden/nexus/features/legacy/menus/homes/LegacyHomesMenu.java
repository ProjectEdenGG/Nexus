package gg.projecteden.nexus.features.legacy.menus.homes;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.legacy.homes.LegacyHome;
import gg.projecteden.nexus.models.legacy.homes.LegacyHomeOwner;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class LegacyHomesMenu extends InventoryProvider {
	private final LegacyHomeOwner homeOwner;

	public LegacyHomesMenu(LegacyHomeOwner homeOwner) {
		this.homeOwner = homeOwner;
		if (homeOwner.getHomes().isEmpty())
			throw new InvalidInputException(homeOwner.getNickname() + " has no legacy homes");
	}

	@Override
	public String getTitle() {
		return homeOwner.getNickname() + "'s Legacy Homes";
	}

	@Override
	public void init() {
		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();

		for (LegacyHome home : homeOwner.getHomes()) {
			ItemBuilder item = new ItemBuilder(Material.BRICKS);

			if (home.getItem() != null && home.getItem().getItemMeta() != null)
				item = new ItemBuilder(home.getItem());

			items.add(ClickableItem.of(item, e -> home.teleportAsync(player)));
		}

		paginator().items(items).build();
	}

}
