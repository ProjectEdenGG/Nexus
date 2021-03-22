package me.pugabyte.nexus.features.minigames.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.managers.ArenaManager;
import me.pugabyte.nexus.features.minigames.mechanics.common.CheckpointMechanic;
import me.pugabyte.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.arenas.CheckpointArena;
import org.bukkit.entity.Player;

import static me.pugabyte.nexus.features.minigames.Minigames.menus;

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

}
