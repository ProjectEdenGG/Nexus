package gg.projecteden.nexus.features.minigames.menus;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.menus.MenuUtils.getRows;

@RequiredArgsConstructor
public class MechanicsMenu extends InventoryProvider {
	private final Arena arena;

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
			.provider(this)
			.title("Game Mechanic Type")
			.rows(getRows(MechanicType.values().length, 1))
			.build()
			.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.of(backItem(), e -> new ArenaMenu(arena).open(player)));
		int row = 1;
		int column = 0;
		for (MechanicType mechanic : MechanicType.values()) {
			ItemStack menuItem = mechanic.get().getMenuItem();

			ItemBuilder item = new ItemBuilder(menuItem.clone())
				.name("&e" + mechanic.get().getName())
				.glow(arena.getMechanicType() == mechanic);

			contents.set(row, column, ClickableItem.of(item, e -> {
				arena.setMechanicType(mechanic);
				arena.write();
				new MechanicsMenu(arena).open(player);

			}));

			if (column != 8) {
				column++;
			} else {
				column = 0;
				row++;
			}
		}

	}

}
