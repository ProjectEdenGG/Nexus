package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.DeathSwap;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.DeathSwapArena;

@CustomMechanicSettings(DeathSwap.class)
public class DeathSwapMenu extends ICustomMechanicMenu {
	private final DeathSwapArena arena;

	public DeathSwapMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, DeathSwapArena.class);
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(player));
	}

}
