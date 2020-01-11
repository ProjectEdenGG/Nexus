package me.pugabyte.bncore.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnpointLocationsMenu extends MenuUtils implements InventoryProvider {

	Arena arena;
	Team team;
	TeamMenus teamMenus = new TeamMenus();

	public SpawnpointLocationsMenu(Arena arena, Team team) {
		this.arena = arena;
		this.team = team;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		////Back Item
		contents.set(0, 0, ClickableItem.of(backItem(), e -> teamMenus.openTeamsEditorMenu(player, arena, team)));

		Pagination page = contents.pagination();

		//Add Spawnpoint Item
		contents.set(0, 4, ClickableItem.of(nameItem(new ItemStack(Material.EMERALD_BLOCK), "&eAdd Spawnpoint", "&3Click to add a spawnpoint||&3at your current location."), e -> {
			team.getSpawnpoints().add(player.getLocation());
			ArenaManager.write(arena);
			teamMenus.openSpawnpointMenu(arena, team).open(player, page.getPage());
		}));

		//Delete Item
		ItemStack deleteItem = nameItem(new ItemStack(Material.TNT), "&cDelete Item", "&7Click me to enter deletion mode.||&7Then, click a spawnpoint with me to||&7delete the spawnpoint.");
		contents.set(0, 8, ClickableItem.of(deleteItem, e -> {
			Utils.wait(2, () -> {
				if (e.getCursor().getType().equals(Material.TNT)) {
					e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
				} else if (e.getCursor().getType() == null || e.getCursor().getType() == Material.AIR) {
					e.getWhoClicked().setItemOnCursor(deleteItem);
				}
			});
		}));

		//Spawnpoint Items
		ClickableItem[] clickableItems = new ClickableItem[team.getSpawnpoints().size()];
		for (Location loc : team.getSpawnpoints()) {
			ItemStack item = nameItem(new ItemStack(Material.COMPASS), "&eSpawnpoint #" + (team.getSpawnpoints().indexOf(loc) + 1),
					"&3x:&e " + (int) arena.getRespawnLocation().getX() +
							"||&3y:&e " + (int) arena.getRespawnLocation().getY() +
							"||&3z&e: " + (int) arena.getRespawnLocation().getZ() +
							"|| ||&7Click to Teleport");

			clickableItems[team.getSpawnpoints().indexOf(loc)] = ClickableItem.of(item, e -> {
				if (e.getCursor().getType().equals(Material.TNT)) {
					Utils.wait(2, () -> {
						team.getSpawnpoints().remove(loc);
						ArenaManager.write(arena);
						e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
						teamMenus.openSpawnpointMenu(arena, team).open(player, page.getPage());
					});
				} else {
					e.getWhoClicked().teleport(loc);
				}
			});
			page.setItems(clickableItems);
			page.setItemsPerPage(36);
			page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

			if (!page.isLast())
				contents.set(0, 8, ClickableItem.of(new ItemStack(nameItem(new ItemStack(Material.ARROW), "&rNext Page")), e -> teamMenus.openSpawnpointMenu(arena, team).open(player, page.next().getPage())));
			if (!page.isFirst())
				contents.set(0, 7, ClickableItem.of(new ItemStack(nameItem(new ItemStack(Material.BARRIER), "&rPrevious Page")), e -> teamMenus.openSpawnpointMenu(arena, team).open(player, page.previous().getPage())));

		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
