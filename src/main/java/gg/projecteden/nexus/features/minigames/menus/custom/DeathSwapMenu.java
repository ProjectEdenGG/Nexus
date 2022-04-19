package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.DeathSwap;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.DeathSwapArena;
import org.bukkit.entity.Player;

@CustomMechanicSettings(DeathSwap.class)
public class DeathSwapMenu extends ICustomMechanicMenu {
	DeathSwapArena arena;

	public DeathSwapMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, DeathSwapArena.class);
		this.arena.write();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		//contents
		contents.set(0, 0, ClickableItem.of(backItem(), e -> new ArenaMenu(arena).open(player)));

		//
	}

}
