package me.pugabyte.nexus.features.minigames.menus.perks;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.menus.PerkMenu;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CategoryMenu<T extends CommonPerksMenu> extends MenuUtils implements InventoryProvider {
	private final Class<T> menu;
	@Getter
	private final SmartInventory inventory = SmartInventory.builder()
			.title("Category Selection")
			.size(3, 9)
			.provider(this)
			.build();

	@Override
	public void open(Player viewer, int page) {
		inventory.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, 1, 1, $ -> new PerkMenu().open(player));
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
