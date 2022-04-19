package gg.projecteden.nexus.features.minigames.menus;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.minigames.Minigames.menus;

public class MechanicsMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public MechanicsMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.of(backItem(), e -> menus.openArenaMenu(player, arena)));
		int row = 1;
		int column = 0;
		for (MechanicType mechanic : MechanicType.values()) {
			ItemStack menuItem = mechanic.get().getMenuItem();
			if (menuItem == null) continue;

			ItemStack item = nameItem(menuItem.clone(), "&e" + mechanic.get().getName());

			if (arena.getMechanicType() == mechanic)
				addGlowing(item);

			contents.set(row, column, ClickableItem.of(item, e -> {
				arena.setMechanicType(mechanic);
				arena.write();
				menus.openMechanicsMenu(player, arena);
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
