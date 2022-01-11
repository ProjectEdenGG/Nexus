package gg.projecteden.nexus.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SpawnpointLocationsMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;
	TeamMenus teamMenus = new TeamMenus();

	public SpawnpointLocationsMenu(@NonNull Arena arena, @NonNull Team team) {
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
		contents.set(0, 8, ClickableItem.from(deleteItem, e -> Tasks.wait(2, () -> {
			if (player.getItemOnCursor().getType().equals(Material.TNT)) {
				player.setItemOnCursor(new ItemStack(Material.AIR));
			} else if (Nullables.isNullOrAir(player.getItemOnCursor())) {
				player.setItemOnCursor(deleteItem);
			}
		})));

		if (team.getSpawnpoints() == null) return;

		ClickableItem[] clickableItems = new ClickableItem[team.getSpawnpoints().size()];
		List<Location> spawnpoints = new ArrayList<>(team.getSpawnpoints());
		for (int i = 0; i < spawnpoints.size(); i++) {
			Location spawnpoint = spawnpoints.get(i);
			ItemStack item = nameItem(Material.COMPASS, "&eSpawnpoint #" + (i + 1),
					getLocationLore(spawnpoints.get(i)) + "|| ||&7Click to Teleport");

			clickableItems[i] = ClickableItem.from(item, e -> {
				if (player.getItemOnCursor().getType().equals(Material.TNT)) {
					Tasks.wait(2, () -> {
						team.getSpawnpoints().remove(spawnpoint);
						arena.write();
						player.setItemOnCursor(new ItemStack(Material.AIR));
						teamMenus.openSpawnpointMenu(arena, team).open(player, page.getPage());
					});
				} else {
					player.teleport(spawnpoint);
				}
			});

			page.setItems(clickableItems);
			page.setItemsPerPage(36);
			page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

			if (!page.isLast())
				contents.set(0, 8, ClickableItem.from(nameItem(Material.ARROW, "&fNext Page"), e -> teamMenus.openSpawnpointMenu(arena, team).open(player, page.next().getPage())));
			if (!page.isFirst())
				contents.set(0, 7, ClickableItem.from(nameItem(Material.BARRIER, "&fPrevious Page"), e -> teamMenus.openSpawnpointMenu(arena, team).open(player, page.previous().getPage())));

		}
	}

}
