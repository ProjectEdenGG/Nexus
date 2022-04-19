package gg.projecteden.nexus.features.menus;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.utils.ColorType;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

import static gg.projecteden.utils.StringUtils.camelCase;

@RequiredArgsConstructor
public class ColorSelectMenu extends InventoryProvider {
	private final Material type;
	private final Consumer<ItemClickData> onClick;

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
			.rows(3)
			.title("Select Color")
			.provider(new ColorSelectMenu(type, onClick))
			.build()
			.open(player, page);
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
