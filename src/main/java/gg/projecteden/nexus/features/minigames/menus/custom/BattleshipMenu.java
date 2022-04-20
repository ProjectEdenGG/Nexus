package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.Battleship;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.BattleshipArena;
import org.bukkit.Material;

@CustomMechanicSettings(Battleship.class)
public class BattleshipMenu extends ICustomMechanicMenu {
	private final BattleshipArena arena;

	public BattleshipMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, BattleshipArena.class);
	}

	@Override
	public void init() {
		contents.set(0, 0, ClickableItem.of(backItem(), e -> new ArenaMenu(arena).open(player)));

		contents.set(1, 0, ClickableItem.empty(Material.BLACK_CONCRETE));
	}

}
