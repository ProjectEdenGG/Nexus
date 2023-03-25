package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.TicTacToe;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.TicTacToeArena;

@CustomMechanicSettings(TicTacToe.class)
public class TicTacToeMenu extends ICustomMechanicMenu {
	private final TicTacToeArena arena;

	public TicTacToeMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, TicTacToeArena.class);
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(viewer));
	}

}
