package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.common.CheckpointMechanic;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.CheckpointArena;
import org.bukkit.entity.Player;

@CustomMechanicSettings(CheckpointMechanic.class)
public class CheckpointMenu extends ICustomMechanicMenu {
	private final CheckpointArena arena;

	public CheckpointMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, CheckpointArena.class);
		this.arena.write();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.of(backItem(), e -> new ArenaMenu(arena).open(player)));
	}

}
