package me.pugabyte.nexus.features.events.aeveonproject.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import me.pugabyte.nexus.features.events.aeveonproject.effects.ClientsideBlocks;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.aeveonproject.AeveonProjectService;
import me.pugabyte.nexus.models.aeveonproject.AeveonProjectUser;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
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
			if (colorType.getDyeColor() == null) continue;

			Material concrete = colorType.getConcrete();
			if (concrete != null) {
				ItemStack color = new ItemBuilder(concrete).name(StringUtils.camelCase(colorType.name())).build();
				contents.set(new SlotPos(row, col), ClickableItem.from(color, e -> {
					user.setShipColor(colorType.getBukkitColor());
					service.save(user);
					e.getPlayer().closeInventory();
					ClientsideBlocks.update(player);
				}));

				if (++col == 8) {
					col = 1;
					++row;
				}
			}

		}

	}
}
