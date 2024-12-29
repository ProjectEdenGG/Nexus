package gg.projecteden.nexus.features.minigames.menus.teams;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.features.menus.api.content.SlotIterator;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.IHasSpawnpoints;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Title("Spawnpoint Locations")
public class SpawnpointLocationsMenu extends InventoryProvider {
	private final Arena arena;
	private final IHasSpawnpoints holder;
	private final InventoryProvider previousMenu;

	@Override
	public void init() {
		addBackItem(previousMenu);

		Pagination page = contents.pagination();

		contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK)
				.name("&eAdd Spawnpoint")
				.lore("&3Click to add a spawnpoint", "&3at your current location."),
			e -> {
				holder.getSpawnpoints().add(viewer.getLocation());
				arena.write();
				refresh();
			}));

		contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.TNT)
				.name("&cDelete Item")
				.lore("&7Click me to enter deletion mode.", "&7Then, click a spawnpoint with me to", "&7delete the spawnpoint."),
			e -> Tasks.wait(2, () -> {
				if (viewer.getItemOnCursor().getType().equals(Material.TNT)) {
					viewer.setItemOnCursor(new ItemStack(Material.AIR));
				} else if (Nullables.isNullOrAir(viewer.getItemOnCursor())) {
					viewer.setItemOnCursor(new ItemBuilder(Material.TNT)
						.name("&cDelete Item")
						.lore(
							"&7Click me to enter deletion mode.",
							"&7Then, click a spawnpoint with me to",
							"&7delete the spawnpoint."
						)
						.build());
				}
			})));

		if (holder.getSpawnpoints() == null) return;

		ClickableItem[] clickableItems = new ClickableItem[holder.getSpawnpoints().size()];
		List<Location> spawnpoints = new ArrayList<>(holder.getSpawnpoints());
		for (int i = 0; i < spawnpoints.size(); i++) {
			Location spawnpoint = spawnpoints.get(i);

			clickableItems[i] = ClickableItem.of(new ItemBuilder(Material.COMPASS)
					.name("&eSpawnpoint #" + (i + 1))
					.lore(MenuUtils.getLocationLore(spawnpoints.get(i)))
					.lore("", "&7Click to Teleport"),
				e -> {
					if (viewer.getItemOnCursor().getType().equals(Material.TNT)) {
						Tasks.wait(2, () -> {
							holder.getSpawnpoints().remove(spawnpoint);
							arena.write();
							viewer.setItemOnCursor(new ItemStack(Material.AIR));
							refresh();
						});
					} else {
						viewer.teleport(spawnpoint);
					}
				});

			page.setItems(clickableItems);
			page.setItemsPerPage(36);
			page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

			if (!page.isLast())
				contents.set(0, 8, ClickableItem.of(Material.ARROW, "&fNext Page", e -> open(viewer, page.next().getPage())));
			if (!page.isFirst())
				contents.set(0, 7, ClickableItem.of(Material.BARRIER, "&fPrevious Page", e -> open(viewer, page.previous().getPage())));

		}
	}

}
