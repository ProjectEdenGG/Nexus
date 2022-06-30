package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.HoleInTheWall;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.HoleInTheWallArena;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.menus.MenuUtils.getLocationLore;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@CustomMechanicSettings(HoleInTheWall.class)
public class HoleInTheWallMenu extends ICustomMechanicMenu {
	private final HoleInTheWallArena arena;

	public HoleInTheWallMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, HoleInTheWallArena.class);
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(player));

		contents.set(1, 0, ClickableItem.of(new ItemBuilder(Material.POTION).name("&eDesign Start Locations"),
			e -> new HoleInTheWallSubMenu(arena).open(player)));
	}

	@Title("Design Start Locations Menu")
	public static class HoleInTheWallSubMenu extends InventoryProvider {
		private final HoleInTheWallArena arena;

		public HoleInTheWallSubMenu(@NonNull Arena arena) {
			this.arena = ArenaManager.convert(arena, HoleInTheWallArena.class);
		}

		@Override
		public void init() {
			HoleInTheWallMenu holeInTheWallMenu = new HoleInTheWallMenu(arena);

			addBackItem(e -> new ArenaMenu(arena).open(player));

			Pagination page = contents.pagination();

			contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK)
					.name("&eAdd Power Up Location")
					.lore("&3Click to add a Power Up", "&3at your current location."),
				e -> {
					arena.getDesignHangerLocation().add(player.getLocation().clone().add(0, -2, 0).getBlock().getLocation());
					arena.write();
					new HoleInTheWallSubMenu(arena).open(player, page.getPage());
				}));

			ItemBuilder deleteItem = new ItemBuilder(Material.TNT)
				.name("&cDelete Item")
				.lore("&7Click me to enter deletion mode.", "&7Then, click a location with", "&7me to delete the location.");
			contents.set(0, 8, ClickableItem.of(deleteItem, e -> Tasks.wait(2, () -> {
				if (player.getItemOnCursor().getType().equals(Material.TNT)) {
					player.setItemOnCursor(new ItemStack(Material.AIR));
				} else if (isNullOrAir(player.getItemOnCursor())) {
					player.setItemOnCursor(deleteItem.build());
				}
			})));

			if (arena.getDesignHangerLocation() == null)
				return;

			paginator().items(new ArrayList<ClickableItem>() {{
				List<Location> designStartLocations = new ArrayList<>(arena.getDesignHangerLocation());
				for (int i = 0; i < designStartLocations.size(); i++) {
					Location designStartLocation = designStartLocations.get(i);

					final ItemBuilder item = new ItemBuilder(Material.COMPASS)
						.name("&eDesign Start Location #" + (i + 1))
						.lore(getLocationLore(designStartLocation))
						.lore("", "&7Click to Teleport");

					add(ClickableItem.of(item, e -> {
						if (player.getItemOnCursor().getType().equals(Material.TNT)) {
							Tasks.wait(2, () -> {
								arena.getDesignHangerLocation().remove(designStartLocation);
								arena.write();
								player.setItemOnCursor(new ItemStack(Material.AIR));
								open(player, page.getPage());
							});
						} else {
							player.teleportAsync(designStartLocation.clone().add(0, 2, 0));
						}
					}));
				}
			}}).build();
		}

	}

}
