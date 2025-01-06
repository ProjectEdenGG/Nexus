package gg.projecteden.nexus.features.menus;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.utils.ColorType;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.function.Consumer;

@Title("Select Color")
@RequiredArgsConstructor
public class ColorSelectMenu extends InventoryProvider {
	private final Material type;
	private final Consumer<ItemClickData> onClick;

	@Override
	public void init() {
		addCloseItem();

		int row = 1;
		int column = 0;

		for (ColorType color : ColorType.values()) {
			if (color.getDyeColor() == null) continue;
			contents.set(row, column, ClickableItem.of(color.switchColor(type), "&e" + StringUtils.camelCase(color.getName()), onClick));
			if (column == 8) {
				column = 0;
				row++;
			} else
				column++;
		}
	}
}
