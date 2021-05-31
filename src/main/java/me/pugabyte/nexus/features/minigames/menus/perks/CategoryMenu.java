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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
	public void init(Player player, InventoryContents contents) {
		List<ClickableItem> items = new ArrayList<>();
		items.add(ClickableItem.from(backItem(), $ -> new PerkMenu().open(player)));
		items.addAll(Arrays.stream(PerkCategory.values())
				.map(perkCategory -> ClickableItem.from(perkCategory.getMenuItem(), $ -> {
					try {
						menu.getConstructor(PerkCategory.class).newInstance(perkCategory);
					} catch (Exception e) {
						throw new RuntimeException("Could not open menu");
					}
				})).collect(Collectors.toList()));
		centerItems(items.toArray(new ClickableItem[]{}), contents, 1);
	}
}
