package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.TurfWars;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.TurfWarsArena;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

@CustomMechanicSettings(TurfWars.class)
public class TurfWarsMenu extends ICustomMechanicMenu {

	TurfWarsArena arena;

	public TurfWarsMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, TurfWarsArena.class);
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(viewer));

		contents.set(12, ClickableItem.of(new ItemBuilder(Material.RED_WOOL).name("&cTeam 1 End Location")
			                                  .lore("&fThe closest point on the turf to team 1's spawn in the center"), e -> {
			arena.setTeam1FloorEnd(e.getPlayer().getLocation());
			ArenaManager.write(arena);
		}));

		contents.set(14, ClickableItem.of(new ItemBuilder(Material.LIGHT_BLUE_WOOL).name("&bTeam 2 End Location")
			                                  .lore("&fThe closest point on the turf to team 2's spawn in the center"), e -> {
			arena.setTeam2FloorEnd(e.getPlayer().getLocation());
			ArenaManager.write(arena);
		}));
	}

}
