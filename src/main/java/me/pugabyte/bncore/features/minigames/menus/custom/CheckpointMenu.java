package me.pugabyte.bncore.features.minigames.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.mechanics.common.CheckpointMechanic;
import me.pugabyte.bncore.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.arenas.CheckpointArena;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.features.minigames.Minigames.menus;

@CustomMechanicSettings(CheckpointMechanic.class)
public class CheckpointMenu extends MenuUtils implements InventoryProvider {
	CheckpointArena arena;

	public CheckpointMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, CheckpointArena.class);
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
