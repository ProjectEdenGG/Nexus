package me.pugabyte.bncore.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.pugabyte.bncore.features.menus.MenuUtils;
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
		addBackItem(contents, e -> teamMenus.openTeamsEditorMenu(player, arena, team));

		Pagination page = contents.pagination();

		contents.set(0, 4, ClickableItem.from(nameItem(
				Material.EMERALD_BLOCK,
				"&eAdd Spawnpoint",
				"&3Click to add a spawnpoint||&3at your current location."
			),
			e -> {
				team.getSpawnpoints().add(player.getLocation());
				arena.write();
				teamMenus.openSpawnpointMenu(arena, team).open(player, page.getPage());
			}));

		ItemStack deleteItem = nameItem(Material.TNT, "&cDelete Item", "&7Click me to enter deletion mode.||&7Then, click a spawnpoint with me to||&7delete the spawnpoint.");
		contents.set(0, 8, ClickableItem.from(deleteItem, e -> Utils.wait(2, () -> {
			if (player.getItemOnCursor().getType().equals(Material.TNT)) {
				player.setItemOnCursor(new ItemStack(Material.AIR));
			} else if (Utils.isNullOrAir(player.getItemOnCursor())) {
				player.setItemOnCursor(deleteItem);
			}
		})));

		if (team.getSpawnpoints() == null) return;

		ClickableItem[] clickableItems = new ClickableItem[team.getSpawnpoints().size()];
		for (Location location : team.getSpawnpoints()) {
			ItemStack item = nameItem(Material.COMPASS, "&eSpawnpoint #" + (team.getSpawnpoints().indexOf(location) + 1),
					getLocationLore(location) + "|| ||&7Click to Teleport");

			clickableItems[team.getSpawnpoints().indexOf(location)] = ClickableItem.from(item, e -> {
				if (player.getItemOnCursor().getType().equals(Material.TNT)) {
					Utils.wait(2, () -> {
						team.getSpawnpoints().remove(location);
						arena.write();
						player.setItemOnCursor(new ItemStack(Material.AIR));
						teamMenus.openSpawnpointMenu(arena, team).open(player, page.getPage());
					});
				} else {
					player.teleport(location);
				}
			});

			page.setItems(clickableItems);
			page.setItemsPerPage(36);
			page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

			if (!page.isLast())
				contents.set(0, 8, ClickableItem.from(new ItemStack(nameItem(new ItemStack(Material.ARROW), "&rNext Page")), e -> teamMenus.openSpawnpointMenu(arena, team).open(player, page.next().getPage())));
			if (!page.isFirst())
				contents.set(0, 7, ClickableItem.from(new ItemStack(nameItem(new ItemStack(Material.BARRIER), "&rPrevious Page")), e -> teamMenus.openSpawnpointMenu(arena, team).open(player, page.previous().getPage())));

		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
