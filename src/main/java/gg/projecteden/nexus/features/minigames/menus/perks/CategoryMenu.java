package gg.projecteden.nexus.features.minigames.menus.perks;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.menus.PerkMenu;
import gg.projecteden.nexus.features.minigames.models.perks.PerkCategory;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

import static gg.projecteden.nexus.utils.StringUtils.plural;

@RequiredArgsConstructor
public class CategoryMenu<T extends CommonPerksMenu> extends MenuUtils implements InventoryProvider {
	private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");

	private final Class<T> menu;
	@Getter
	private final SmartInventory inventory = SmartInventory.builder()
		.title("Category Selection")
		.size(3, 9)
		.provider(this)
		.build();

	@Override
	public void open(Player player, int page) {
		inventory.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, 1, 1, $ -> new PerkMenu().open(player));

		if (menu.equals(BuyPerksMenu.class)) {
			PerkOwner perkOwner = new PerkOwnerService().get(player);
			contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.EMERALD).name("&2&lBalance")
				.lore("&f" + FORMATTER.format(perkOwner.getTokens()) + plural(" token", perkOwner.getTokens()))
				.build()));
		}

		int col = 2;
		for (PerkCategory perkCategory : PerkCategory.values()) {
			contents.set(1, col, ClickableItem.from(perkCategory.getMenuItem(), $ -> {
				try {
					menu.getConstructor(PerkCategory.class).newInstance(perkCategory).open(player);
				} catch (Exception e) {
					throw new RuntimeException("Could not open menu");
				}
			}));
			col += 1;
		}
	}
}
