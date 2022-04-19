package gg.projecteden.nexus.features.events.aeveonproject.menus;

import gg.projecteden.nexus.features.events.aeveonproject.effects.ClientsideBlocks;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.models.aeveonproject.AeveonProjectService;
import gg.projecteden.nexus.models.aeveonproject.AeveonProjectUser;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
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
