package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.Thimble;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.ThimbleArena;

@CustomMechanicSettings(Thimble.class)
public class ThimbleMenu extends ICustomMechanicMenu {
	ThimbleArena arena;

	public ThimbleMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, ThimbleArena.class);
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(player));
	}

}
