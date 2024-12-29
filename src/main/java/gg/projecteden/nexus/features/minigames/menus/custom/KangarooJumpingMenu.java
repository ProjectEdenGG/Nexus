package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.KangarooJumping;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.KangarooJumpingArena;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@CustomMechanicSettings(KangarooJumping.class)
public class KangarooJumpingMenu extends ICustomMechanicMenu {
	private final KangarooJumpingArena arena;

	public KangarooJumpingMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, KangarooJumpingArena.class);
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(viewer));

		contents.set(1, 0, ClickableItem.of(new ItemBuilder(Material.POTION).name("&ePower Up Locations"),
			e -> new KangarooJumpingSubMenu(arena).open(viewer)));
	}

	@RequiredArgsConstructor
	@Title("Power Up Locations Menu")
	public static class KangarooJumpingSubMenu extends InventoryProvider {
		private final KangarooJumpingArena arena;

		@Override
		public void init() {
			addBackItem(e -> new ArenaMenu(arena).open(viewer));

			Pagination page = contents.pagination();

			contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK)
						.name("&eAdd Power Up Location")
					.lore("&3Click to add a Power Up", "&3at your current location."),
				e -> {
					arena.getPowerUpLocations().add(viewer.getLocation());
					arena.write();
					new KangarooJumpingSubMenu(arena).open(viewer, page.getPage());
				}));

			ItemBuilder deleteItem = new ItemBuilder(Material.TNT)
				.name("&cDelete Item")
				.lore("&7Click me to enter deletion mode.", "&7Then, click a power up location with", "&7me to delete the location.");
			contents.set(0, 8, ClickableItem.of(deleteItem, e -> Tasks.wait(2, () -> {
				if (viewer.getItemOnCursor().getType().equals(Material.TNT)) {
					viewer.setItemOnCursor(new ItemStack(Material.AIR));
				} else if (Nullables.isNullOrAir(viewer.getItemOnCursor())) {
					viewer.setItemOnCursor(deleteItem.build());
				}
			})));

			if (arena.getPowerUpLocations() == null)
				return;

			paginator().items(new ArrayList<ClickableItem>() {{
				List<Location> powerUpLocations = new ArrayList<>(arena.getPowerUpLocations());
				for (int i = 0; i < powerUpLocations.size(); i++) {
					Location powerUpLocation = powerUpLocations.get(i);

					ItemBuilder item = new ItemBuilder(Material.COMPASS).name("&ePower Up #" + (i + 1))
						.lore(MenuUtils.getLocationLore(powerUpLocation))
						.lore("", "&7Click to Teleport");

					add(ClickableItem.of(item, e -> {
						if (viewer.getItemOnCursor().getType().equals(Material.TNT)) {
							Tasks.wait(2, () -> {
								arena.getPowerUpLocations().remove(powerUpLocation);
								arena.write();
								viewer.setItemOnCursor(new ItemStack(Material.AIR));
								open(viewer, page.getPage());
							});
						} else {
							viewer.teleportAsync(powerUpLocation);
						}
					}));
				}
			}}).build();
		}

	}
}
