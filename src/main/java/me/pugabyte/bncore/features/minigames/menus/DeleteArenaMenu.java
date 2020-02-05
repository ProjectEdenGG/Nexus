package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.features.minigames.Minigames.menus;

public class DeleteArenaMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public DeleteArenaMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		ItemStack cancelItem = nameItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorType.LIGHT_GREEN.getDurability().shortValue()), "&7Cancel");
		contents.fillRect(0, 0, 2, 8, ClickableItem.from(cancelItem, e -> menus.openArenaMenu(player, arena)));
		contents.fillRect(1, 1, 1, 7, ClickableItem.from(cancelItem, e -> menus.openArenaMenu(player, arena)));

		contents.set(1, 4, ClickableItem.from(nameItem(Material.TNT, "&4&lDELETE ARENA", "&7This cannot be undone."),
				e -> {
					arena.delete();
					player.sendMessage(Minigames.PREFIX + "Arena &e" + arena.getName() + " &3deleted");
				}));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
