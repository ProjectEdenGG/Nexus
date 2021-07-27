package gg.projecteden.nexus.features.minigames.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.HoleInTheWall;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.HoleInTheWallArena;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@CustomMechanicSettings(HoleInTheWall.class)
public class HoleInTheWallMenu extends MenuUtils implements InventoryProvider {

	HoleInTheWallArena arena;

	public HoleInTheWallMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, HoleInTheWallArena.class);
	}

	public SmartInventory openDesignStartLocationsMenu(Arena arena){
		return SmartInventory.builder()
				.size(6, 9)
				.provider(new HoleInTheWallSubMenu(arena))
				.title("Design Start Locations Menu")
				.build();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> Minigames.menus.openArenaMenu(player, arena)));

		contents.set(1, 0, ClickableItem.from(nameItem(new ItemStack(Material.POTION), "&eDesign Start Locations"),
				e -> openDesignStartLocationsMenu(arena).open(player)));
	}

	public static class HoleInTheWallSubMenu extends MenuUtils implements InventoryProvider {
		HoleInTheWallArena arena;

		public HoleInTheWallSubMenu(@NonNull Arena arena) {
			this.arena = ArenaManager.convert(arena, HoleInTheWallArena.class);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			HoleInTheWallMenu holeInTheWallMenu = new HoleInTheWallMenu(arena);

			addBackItem(contents, e -> Minigames.menus.openArenaMenu(player, arena));

			Pagination page = contents.pagination();

			contents.set(0, 4, ClickableItem.from(nameItem(
					Material.EMERALD_BLOCK,
					"&eAdd Power Up Location",
					"&3Click to add a Power Up||&3at your current location."
				),
				e -> {
					arena.getDesignHangerLocation().add(player.getLocation().clone().add(0, -2, 0).getBlock().getLocation());
					arena.write();
					holeInTheWallMenu.openDesignStartLocationsMenu(arena).open(player, page.getPage());
				}));

			ItemStack deleteItem = nameItem(Material.TNT, "&cDelete Item", "&7Click me to enter deletion mode." +
					"||&7Then, click a location with||&7me to delete the location.");
			contents.set(0, 8, ClickableItem.from(deleteItem, e -> Tasks.wait(2, () -> {
				if (player.getItemOnCursor().getType().equals(Material.TNT)) {
					player.setItemOnCursor(new ItemStack(Material.AIR));
				} else if (ItemUtils.isNullOrAir(player.getItemOnCursor())) {
					player.setItemOnCursor(deleteItem);
				}
			})));

			if (arena.getDesignHangerLocation() == null) return;

			ClickableItem[] clickableItems = new ClickableItem[arena.getDesignHangerLocation().size()];
			List<Location> designStartLocations = new ArrayList<>(arena.getDesignHangerLocation());
			for (int i = 0; i < designStartLocations.size(); i++) {
				Location designStartLocation = designStartLocations.get(i);
				ItemStack item = nameItem(Material.COMPASS, "&eDesign Start Location #" + (i + 1),
						getLocationLore(designStartLocations.get(i)) + "|| ||&7Click to Teleport");

				clickableItems[i] = ClickableItem.from(item, e -> {
					if (player.getItemOnCursor().getType().equals(Material.TNT)) {
						Tasks.wait(2, () -> {
							arena.getDesignHangerLocation().remove(designStartLocation);
							arena.write();
							player.setItemOnCursor(new ItemStack(Material.AIR));
							holeInTheWallMenu.openDesignStartLocationsMenu(arena).open(player, page.getPage());
						});
					} else {
						player.teleportAsync(designStartLocation.clone().add(0, 2, 0));
					}
				});
			}

			page.setItems(clickableItems);
			page.setItemsPerPage(36);
			page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

			if (!page.isLast())
				contents.set(0, 8, ClickableItem.from(nameItem(new ItemStack(Material.ARROW), "&fNext Page"), e ->
						holeInTheWallMenu.openDesignStartLocationsMenu(arena).open(player, page.next().getPage())));
			if (!page.isFirst())
				contents.set(0, 7, ClickableItem.from(nameItem(new ItemStack(Material.BARRIER), "&fPrevious Page"), e ->
						holeInTheWallMenu.openDesignStartLocationsMenu(arena).open(player, page.previous().getPage())));
		}

	}
}
