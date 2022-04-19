package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.Thimble;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.ThimbleArena;
import org.bukkit.entity.Player;

@CustomMechanicSettings(Thimble.class)
public class ThimbleMenu extends ICustomMechanicMenu {
	ThimbleArena arena;

	public ThimbleMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, ThimbleArena.class);
		this.arena.write();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.of(backItem(), e -> new ArenaMenu(arena).open(player)));
	}

}
