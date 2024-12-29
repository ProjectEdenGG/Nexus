package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.Murder;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.MechanicsMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.MurderArena;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@CustomMechanicSettings(Murder.class)
public class MurderMenu extends ICustomMechanicMenu {
	private final MurderArena arena;

	public MurderMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, MurderArena.class);
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> MechanicsMenu.openCustomSettingsMenu(player, arena)));
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(viewer));

		contents.set(1, 4, ClickableItem.of(new ItemBuilder(Material.IRON_INGOT).name("&eScrap Points"),
			e -> new MurderScrapsMenu(arena).open(viewer)));
	}

	@RequiredArgsConstructor
	@Title("Scrap Points Locations Menu")
	public static class MurderScrapsMenu extends InventoryProvider {
		private final MurderArena arena;

		@Override
		public void init() {
			addBackItem(e -> new ArenaMenu(arena).open(viewer));

			Pagination page = contents.pagination();

			contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK)
					.name("&eAdd Scrap Point Location")
					.lore("&3Click to add a Scrap Point", "&3at your current location."),
				e -> {
					arena.getScrapPoints().add(viewer.getLocation().getBlock().getLocation());
					arena.write();
					new MurderScrapsMenu(arena).open(viewer, page.getPage());
				}));

			ItemBuilder deleteItem = new ItemBuilder(Material.TNT)
				.name("&cDelete Item")
				.lore("&7Click me to enter deletion mode.", "&7Then, click a Scrap Point location with", "&7me to delete the location.");

			contents.set(0, 8, ClickableItem.of(deleteItem, e -> Tasks.wait(2, () -> {
				if (viewer.getItemOnCursor().getType().equals(Material.TNT)) {
					viewer.setItemOnCursor(new ItemStack(Material.AIR));
				} else if (Nullables.isNullOrAir(viewer.getItemOnCursor())) {
					viewer.setItemOnCursor(deleteItem.build());
				}
			})));

			if (arena.getScrapPoints() == null)
				return;

			paginator().items(new ArrayList<ClickableItem>() {{
				List<Location> scrapPointsLocations = new ArrayList<>(arena.getScrapPoints());
				for (int i = 0; i < scrapPointsLocations.size(); i++) {
					Location scrapPointsLocation = scrapPointsLocations.get(i);
					ItemBuilder item = new ItemBuilder(Material.COMPASS)
						.name("&eScrap Point #" + (i + 1))
						.lore(MenuUtils.getLocationLore(scrapPointsLocations.get(i)))
						.lore("", "&7Click to Teleport");

					add(ClickableItem.of(item, e -> {
						if (viewer.getItemOnCursor().getType().equals(Material.TNT)) {
							Tasks.wait(2, () -> {
								arena.getScrapPoints().remove(scrapPointsLocation);
								arena.write();
								viewer.setItemOnCursor(new ItemStack(Material.AIR));
								new MurderScrapsMenu(arena).open(viewer, page.getPage());
							});
						} else {
							viewer.teleportAsync(scrapPointsLocation);
						}
					}));
				}
			}}).build();
		}

	}

}
