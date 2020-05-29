package me.pugabyte.bncore.features.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.ItemClickData;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ColorSelectMenu extends MenuUtils implements InventoryProvider {

	public String type;
	Consumer<ItemClickData> onClick;

	public ColorSelectMenu(String type, Consumer<ItemClickData> onClick) {
		this.type = type;
		this.onClick = onClick;
	}

	@Override
	public void init(Player player, InventoryContents contents) {

		addCloseItem(contents);

		int row = 1;
		int column = 0;

		for (ColorType color : ColorType.values()) {
			if (color == ColorType.LIGHT_RED) continue;
			ItemStack item = nameItem(Material.valueOf(color.getName().replace(" ", "_").toUpperCase() + "_" +
					type.toUpperCase()), "&e" + StringUtils.camelCase(color.getName()));
			contents.set(row, column, ClickableItem.from(item, e -> onClick.accept(e)));
			if (column == 8) {
				column = 0;
				row++;
			} else
				column++;
		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
