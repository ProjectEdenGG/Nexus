package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.GrabAJumbuck;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.MechanicsMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.GrabAJumbuckArena;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CustomMechanicSettings(GrabAJumbuck.class)
public class GrabAJumbuckMenu extends ICustomMechanicMenu {
	private final GrabAJumbuckArena arena;

	public GrabAJumbuckMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, GrabAJumbuckArena.class);
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(viewer));

		contents.set(2, 8, ClickableItem.of(new ItemBuilder(Material.ITEM_FRAME)
				.name("&eAdd Item")
				.lore("&3Click me with an item", "&3in your hand to add it."),
			e -> Tasks.wait(2, () -> {
				if (Nullables.isNullOrAir(viewer.getItemOnCursor())) return;
				if (arena.getSheepSpawnBlocks().size() == 9) {
					PlayerUtils.send(viewer, Minigames.PREFIX + "The max amount of blocks has already been set.");
					return;
				}
				arena.getSheepSpawnBlocks().add(viewer.getItemOnCursor().getType());
				viewer.setItemOnCursor(new ItemStack(Material.AIR));
				arena.write();
				MechanicsMenu.openCustomSettingsMenu(viewer, arena);
			})
		));

		List<Material> sortedList = new ArrayList<>(arena.getSheepSpawnBlocks());
		Collections.sort(sortedList);
		int column = 0;
		for (int i = 0; i < sortedList.size(); i++) {
			contents.set(1, column, ClickableItem.of(new ItemBuilder(sortedList.get(i))
					.name("&e" + sortedList.get(i).name())
					.lore("&3Click me to remove this", "&3material from the list."),
				e -> {
					arena.getSheepSpawnBlocks().remove(((InventoryClickEvent) e.getEvent()).getCurrentItem().getType());
					arena.write();
					MechanicsMenu.openCustomSettingsMenu(viewer, arena);
				}));
			column++;
		}
	}

}
