package gg.projecteden.nexus.features.menus;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

import static gg.projecteden.utils.StringUtils.camelCase;

public class ColorSelectMenu extends MenuUtils implements InventoryProvider {

	public Material type;
	Consumer<ItemClickData> onClick;

	public ColorSelectMenu(Material type, Consumer<ItemClickData> onClick) {
		this.type = type;
		this.onClick = onClick;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents);

		int row = 1;
		int column = 0;

		for (ColorType color : ColorType.values()) {
			if (color.getDyeColor() == null) continue;
			contents.set(row, column, ClickableItem.of(color.switchColor(type), "&e" + camelCase(color.getName()), e -> onClick.accept(e)));
			if (column == 8) {
				column = 0;
				row++;
			} else
				column++;
		}
	}
}
