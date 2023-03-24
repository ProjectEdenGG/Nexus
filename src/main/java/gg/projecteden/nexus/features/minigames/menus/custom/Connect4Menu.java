package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.Connect4;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.Connect4Arena;

@CustomMechanicSettings(Connect4.class)
public class Connect4Menu extends ICustomMechanicMenu {
	private final Connect4Arena arena;

	public Connect4Menu(Arena arena) {
		this.arena = ArenaManager.convert(arena, Connect4Arena.class);
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(viewer));
	}

}
