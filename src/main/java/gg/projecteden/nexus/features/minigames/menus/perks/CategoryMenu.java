package gg.projecteden.nexus.features.minigames.menus.perks;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.menus.PerkMenu;
import gg.projecteden.nexus.features.minigames.models.perks.PerkCategory;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.text.DecimalFormat;

import static gg.projecteden.nexus.utils.StringUtils.plural;

@Rows(3)
@Title("Category Selection")
@RequiredArgsConstructor
public class CategoryMenu<T extends CommonPerksMenu> extends InventoryProvider {
	private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");

	private final Class<T> menu;

	@Override
	public void init() {
		addBackItem(1, 1, $ -> new PerkMenu().open(viewer));

		if (menu.equals(BuyPerksMenu.class)) {
			PerkOwner perkOwner = new PerkOwnerService().get(viewer);
			contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.EMERALD).name("&2&lBalance")
				.lore("&f" + FORMATTER.format(perkOwner.getTokens()) + plural(" token", perkOwner.getTokens()))
				.build()));
		}

		int col = 2;
		for (PerkCategory perkCategory : PerkCategory.values()) {
			contents.set(1, col, ClickableItem.of(perkCategory.getMenuItem(), $ -> {
				try {
					menu.getConstructor(PerkCategory.class).newInstance(perkCategory).open(viewer);
				} catch (Exception e) {
					throw new RuntimeException("Could not open menu");
				}
			}));
			col += 1;
		}
	}
}
