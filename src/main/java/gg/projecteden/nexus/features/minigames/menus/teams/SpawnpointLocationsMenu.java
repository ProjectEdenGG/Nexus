package gg.projecteden.nexus.features.minigames.menus.teams;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.features.menus.api.content.SlotIterator;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.menus.MenuUtils.getLocationLore;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@RequiredArgsConstructor
@Title("Spawnpoint Location Menus")
public class SpawnpointLocationsMenu extends InventoryProvider {
	private final Arena arena;
	private final Team team;

	@Override
	public void init() {
		addBackItem(e -> new TeamEditorMenu(arena, team).open(player));

		Pagination page = contents.pagination();

		contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK)
				.name("&eAdd Spawnpoint")
				.lore("&3Click to add a spawnpoint", "&3at your current location."),
			e -> {
				team.getSpawnpoints().add(player.getLocation());
				arena.write();
				new SpawnpointLocationsMenu(arena, team).open(player, page.getPage());
			}));

		contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.TNT)
				.name("&cDelete Item")
				.lore("&7Click me to enter deletion mode.", "&7Then, click a spawnpoint with me to", "&7delete the spawnpoint."),
			e -> Tasks.wait(2, () -> {
				if (player.getItemOnCursor().getType().equals(Material.TNT)) {
					player.setItemOnCursor(new ItemStack(Material.AIR));
				} else if (isNullOrAir(player.getItemOnCursor())) {
					player.setItemOnCursor(new ItemBuilder(Material.TNT)
						.name("&cDelete Item")
						.lore(
							"&7Click me to enter deletion mode.",
							"&7Then, click a spawnpoint with me to",
							"&7delete the spawnpoint."
						)
						.build());
				}
			})));

		if (team.getSpawnpoints() == null) return;

		ClickableItem[] clickableItems = new ClickableItem[team.getSpawnpoints().size()];
		List<Location> spawnpoints = new ArrayList<>(team.getSpawnpoints());
		for (int i = 0; i < spawnpoints.size(); i++) {
			Location spawnpoint = spawnpoints.get(i);

			clickableItems[i] = ClickableItem.of(new ItemBuilder(Material.COMPASS)
					.name("&eSpawnpoint #" + (i + 1))
					.lore(getLocationLore(spawnpoints.get(i)))
					.lore("", "&7Click to Teleport"),
				e -> {
					if (player.getItemOnCursor().getType().equals(Material.TNT)) {
						Tasks.wait(2, () -> {
							team.getSpawnpoints().remove(spawnpoint);
							arena.write();
							player.setItemOnCursor(new ItemStack(Material.AIR));
							new SpawnpointLocationsMenu(arena, team).open(player, page.getPage());
						});
					} else {
						player.teleport(spawnpoint);
					}
				});

			page.setItems(clickableItems);
			page.setItemsPerPage(36);
			page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

			if (!page.isLast())
				contents.set(0, 8, ClickableItem.of(Material.ARROW, "&fNext Page", e -> new SpawnpointLocationsMenu(arena, team).open(player, page.next().getPage())));
			if (!page.isFirst())
				contents.set(0, 7, ClickableItem.of(Material.BARRIER, "&fPrevious Page", e -> new SpawnpointLocationsMenu(arena, team).open(player, page.previous().getPage())));

		}
	}

}
