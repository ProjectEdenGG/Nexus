package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.Dropper;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.menus.teams.SpawnpointLocationsMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.DropperArena;
import gg.projecteden.nexus.features.minigames.models.arenas.DropperMap;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

@Title("Dropper")
@CustomMechanicSettings(Dropper.class)
public class DropperMenu extends ICustomMechanicMenu {
	private final DropperArena arena;

	public DropperMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, DropperArena.class);
	}

	@Override
	public void init() {
		addBackItem(new ArenaMenu(arena));

		contents.set(0, 8, ClickableItem.of(new ItemBuilder(ItemModelType.GUI_PLUS)
			.dyeColor(Color.LIME)
			.itemFlags(ItemFlag.HIDE_DYE)
			.name("&eCreate Map"), e -> {
				MenuUtils.openAnvilMenu(viewer, "",
					(player, input) -> {
						final DropperMap map = new DropperMap();
						map.setName(input);
						arena.getMaps().add(map);
						arena.write();
						player.closeInventory();
						Tasks.wait(1, () -> new DropperMapMenu(arena, map, this).open(viewer));
						return AnvilGUI.Response.text(input);
					},
					onClose -> {
						Nexus.log("Testing");
						refresh();
					});
			}));

		List<ClickableItem> items = new ArrayList<>();

		for (DropperMap map : arena.getMaps()) {
			items.add(ClickableItem.of(new ItemBuilder(Material.MAP).name("&e" + map.getName()), e -> {
				new DropperMapMenu(arena, map, this).open(viewer);
			}));
		}

		paginate(items);
	}

	@RequiredArgsConstructor
	public static class DropperMapMenu extends InventoryProvider {
		private final DropperArena arena;
		private final DropperMap map;
		private final InventoryProvider previousMenu;

		@Override
		public String getTitle() {
			return map.getName();
		}

		@Override
		public void init() {
			addBackItem(previousMenu);

			contents.set(0, 8, ClickableItem.of(Material.TNT, "&cDelete Map", e -> {
				ConfirmationMenu.builder()
					.onConfirm(e2 -> {
						arena.getMaps().remove(map);
						arena.write();
						previousMenu.open(viewer);
					})
					.onCancel(e2 -> refresh())
					.open(viewer);
			}));

			List<ClickableItem> items = new ArrayList<>();

			items.add(ClickableItem.of(Material.NAME_TAG, "&eName", e -> {
				MenuUtils.openAnvilMenu(viewer, map.getName(),
					(player, input) -> {
						map.setName(input);
						arena.write();
						refresh();
						return AnvilGUI.Response.text(input);
					},
					onClose -> refresh());
			}));

			items.add(ClickableItem.of(new ItemBuilder(Material.COMPASS).name("&eSpawnpoint Locations"), e ->
				new SpawnpointLocationsMenu(arena, map, this).open(viewer)));

			items.add(ClickableItem.of(new ItemBuilder(Material.SPYGLASS)
				.name("&eSpectate Location")
				.lore(MenuUtils.getLocationLore(map.getSpectateLocation())),
				e -> {
					arena.setSpectateLocation(viewer.getLocation());
					arena.write();
					open(viewer);
				}));

			paginate(items);
		}

	}

}
