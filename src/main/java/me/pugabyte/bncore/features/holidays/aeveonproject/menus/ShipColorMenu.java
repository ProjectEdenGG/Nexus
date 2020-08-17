package me.pugabyte.bncore.features.holidays.aeveonproject.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectService;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectUser;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShipColorMenu extends MenuUtils implements InventoryProvider {
	AeveonProjectService service = new AeveonProjectService();
	AeveonProjectUser user;

	public static SmartInventory getInv() {
		return SmartInventory.builder()
				.provider(new ShipColorMenu())
				.size(5, 9)
				.title(ChatColor.DARK_AQUA + "Customize your ship color:")
				.closeable(true)
				.build();
	}

	public void open(Player player) {
		getInv().open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		user = service.get(player);

		int row = 1;
		int col = 1;

		addCloseItem(contents);

		for (ColorType colorType : ColorType.values()) {
			Material concrete = colorType.getConcrete();
			if (concrete != null) {
				ItemStack color = new ItemBuilder(concrete).name(StringUtils.camelCase(colorType.name())).build();
				contents.set(new SlotPos(row, col), ClickableItem.from(color, e -> {
					user.setShipColor(colorType.getColor());
					service.save(user);
					e.getPlayer().closeInventory();
				}));

				if (++col == 8) {
					col = 1;
					++row;
				}
			}

		}

	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}
}
