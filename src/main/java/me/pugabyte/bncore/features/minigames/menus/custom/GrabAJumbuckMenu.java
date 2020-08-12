package me.pugabyte.bncore.features.minigames.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.mechanics.GrabAJumbuck;
import me.pugabyte.bncore.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.arenas.GrabAJumbuckArena;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.bncore.features.minigames.Minigames.menus;

@CustomMechanicSettings(GrabAJumbuck.class)
public class GrabAJumbuckMenu extends MenuUtils implements InventoryProvider {

	GrabAJumbuckArena arena;

	public GrabAJumbuckMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, GrabAJumbuckArena.class);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openArenaMenu(player, arena)));

		contents.set(2, 8, ClickableItem.from(nameItem(
				Material.ITEM_FRAME,
				"&eAdd Item",
				"&3Click me with an item||&3in your hand to add it."
				),
				e -> Tasks.wait(2, () -> {
					if (Utils.isNullOrAir(player.getItemOnCursor())) return;
					if (arena.getSheepSpawnBlocks().size() == 9) {
						Utils.send(player, Minigames.PREFIX + "The max amount of blocks has already been set.");
						return;
					}
					arena.getSheepSpawnBlocks().add(player.getItemOnCursor().getType());
					player.setItemOnCursor(new ItemStack(Material.AIR));
					arena.write();
					menus.openCustomSettingsMenu(player, arena);
				})
		));

		List<Material> sortedList = new ArrayList<>(arena.getSheepSpawnBlocks());
		Collections.sort(sortedList);
		int column = 0;
		for (int i = 0; i < sortedList.size(); i++) {
			contents.set(1, column, ClickableItem.from(nameItem(new ItemStack(sortedList.get(i)),
					"&e" + sortedList.get(i).name(),
					"&3Click me to remove this||&3material from the list."
					),
					e -> {
						arena.getSheepSpawnBlocks().remove(((InventoryClickEvent) e.getEvent()).getCurrentItem().getType());
						arena.write();
						menus.openCustomSettingsMenu(player, arena);
					}));
			column++;
		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
