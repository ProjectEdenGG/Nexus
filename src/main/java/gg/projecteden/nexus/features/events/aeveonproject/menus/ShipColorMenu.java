package gg.projecteden.nexus.features.events.aeveonproject.menus;

import gg.projecteden.nexus.features.events.aeveonproject.effects.ClientsideBlocks;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.models.aeveonproject.AeveonProjectService;
import gg.projecteden.nexus.models.aeveonproject.AeveonProjectUser;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Rows(5)
@Title("Customize your ship color:")
public class ShipColorMenu extends InventoryProvider {
	private final AeveonProjectService service = new AeveonProjectService();
	private AeveonProjectUser user;

	@Override
	public void init() {
		user = service.get(player);

		int row = 1;
		int col = 1;

		addCloseItem();

		for (ColorType colorType : ColorType.values()) {
			if (colorType.getDyeColor() == null) continue;

			Material concrete = colorType.getConcrete();
			if (concrete != null) {
				ItemStack color = new ItemBuilder(concrete).name(StringUtils.camelCase(colorType.name())).build();
				contents.set(new SlotPos(row, col), ClickableItem.of(color, e -> {
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
