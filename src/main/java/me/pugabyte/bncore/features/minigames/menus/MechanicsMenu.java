package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.features.minigames.Minigames.menus;

public class MechanicsMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public MechanicsMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openArenaMenu(player, arena)));
		int row = 1;
		int column = 0;
		for (MechanicType mechanic : MechanicType.values()) {
			ItemStack menuItem = mechanic.get().getMenuItem();
			if (menuItem == null) continue;

			ItemStack item = nameItem(menuItem.clone(), "&e" + mechanic.get().getName());

			if (arena.getMechanicType() == mechanic)
				addGlowing(item);

			contents.set(row, column, ClickableItem.from(item, e -> {
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

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

}
