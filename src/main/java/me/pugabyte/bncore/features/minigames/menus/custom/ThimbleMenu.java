package me.pugabyte.bncore.features.minigames.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.arenas.ThimbleArena;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.features.minigames.Minigames.menus;

@CustomMechanicSettings(MechanicType.THIMBLE)
public class ThimbleMenu extends MenuUtils implements InventoryProvider {
	ThimbleArena arena;

	public ThimbleMenu(Arena arena) {
		this.arena = (ThimbleArena) ArenaManager.convert(arena, ThimbleArena.class);
		this.arena.write();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openArenaMenu(player, arena)));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
