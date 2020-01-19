package me.pugabyte.bncore.features.minigames.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bncore.features.minigames.menus.custom.kangaroojumping.KangarooJumpingSubMenu;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.arenas.KangarooJumpingArena;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CustomMechanicSettings(MechanicType.KANGAROO_JUMPING)
public class KangarooJumpingMenu extends MenuUtils implements InventoryProvider {

	KangarooJumpingArena arena;

	public KangarooJumpingMenu(Arena arena) {
		this.arena = (KangarooJumpingArena) ArenaManager.convert(arena, KangarooJumpingArena.class);
	}

	public SmartInventory openPowerUpLocationsMenu(Arena arena){
		SmartInventory INV = SmartInventory.builder()
				.id("powerUpLocationsMenu")
				.size(6, 9)
				.provider(new KangarooJumpingSubMenu(arena))
				.title("Power Up Locations Menu")
				.build();
		return INV;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> Minigames.menus.openArenaMenu(player, arena)));

		contents.set(1, 0, ClickableItem.from(nameItem(new ItemStack(Material.POTION), "&ePower Up Locations"),
				e -> openPowerUpLocationsMenu(arena).open(player)));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
