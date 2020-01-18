package me.pugabyte.bncore.features.minigames.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.arenas.DeathSwapArena;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.features.minigames.Minigames.menus;

@CustomMechanicSettings(MechanicType.DEATH_SWAP)
public class DeathSwapMenu extends MenuUtils implements InventoryProvider {
	DeathSwapArena arena;

	public DeathSwapMenu(Arena arena) {
		this.arena = (DeathSwapArena) ArenaManager.convert(arena, DeathSwapArena.class);
		this.arena.write();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		//contents
		contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openArenaMenu(player, arena)));

		//
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
