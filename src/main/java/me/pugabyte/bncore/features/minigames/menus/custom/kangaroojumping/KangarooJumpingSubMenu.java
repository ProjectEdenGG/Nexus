package me.pugabyte.bncore.features.minigames.menus.custom.kangaroojumping;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import lombok.NonNull;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.custom.KangarooJumpingMenu;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.arenas.KangarooJumpingArena;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KangarooJumpingSubMenu extends MenuUtils implements InventoryProvider {
	KangarooJumpingArena arena;

	public KangarooJumpingSubMenu(@NonNull Arena arena) {
		this.arena = (KangarooJumpingArena) ArenaManager.convert(arena, KangarooJumpingArena.class);
	}

	@Override
	public void init(Player player, InventoryContents contents) {

		KangarooJumpingMenu kangarooJumpingMenu = new KangarooJumpingMenu(arena);

		addBackItem(contents, e -> Minigames.menus.openArenaMenu(player, arena));

		Pagination page = contents.pagination();

		contents.set(0, 4, ClickableItem.from(nameItem(
				Material.EMERALD_BLOCK,
				"&eAdd Power Up Location",
				"&3Click to add a Power Up||&3at your current location."
			),
			e -> {
				arena.getPowerUpLocations().add(player.getLocation());
				arena.write();
				kangarooJumpingMenu.openPowerUpLocationsMenu(arena).open(player, page.getPage());
			}));

		ItemStack deleteItem = nameItem(Material.TNT, "&cDelete Item", "&7Click me to enter deletion mode.||&7Then, click a power up location with||&7me to delete the location.");
		contents.set(0, 8, ClickableItem.from(deleteItem, e -> Tasks.wait(2, () -> {
			if (player.getItemOnCursor().getType().equals(Material.TNT)) {
				player.setItemOnCursor(new ItemStack(Material.AIR));
			} else if (Utils.isNullOrAir(player.getItemOnCursor())) {
				player.setItemOnCursor(deleteItem);
			}
		})));

		if (arena.getPowerUpLocations() == null) return;

		ClickableItem[] clickableItems = new ClickableItem[arena.getPowerUpLocations().size()];
		List<Location> powerUpLocations = new ArrayList<>(arena.getPowerUpLocations());
		for (int i = 0; i < powerUpLocations.size(); i++) {
			Location powerUpLocation = powerUpLocations.get(i);
			ItemStack item = nameItem(Material.COMPASS, "&ePower Up #" + (i + 1),
					getLocationLore(powerUpLocations.get(i)) + "|| ||&7Click to Teleport");

			clickableItems[i] = ClickableItem.from(item, e -> {
				if (player.getItemOnCursor().getType().equals(Material.TNT)) {
					Tasks.wait(2, () -> {
						arena.getPowerUpLocations().remove(powerUpLocation);
						arena.write();
						player.setItemOnCursor(new ItemStack(Material.AIR));
						kangarooJumpingMenu.openPowerUpLocationsMenu(arena).open(player, page.getPage());
					});
				} else {
					player.teleport(powerUpLocation);
				}
			});

			page.setItems(clickableItems);
			page.setItemsPerPage(36);
			page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

			if (!page.isLast())
				contents.set(0, 8, ClickableItem.from(new ItemStack(nameItem(new ItemStack(Material.ARROW), "&rNext Page")), e -> kangarooJumpingMenu.openPowerUpLocationsMenu(arena).open(player, page.next().getPage())));
			if (!page.isFirst())
				contents.set(0, 7, ClickableItem.from(new ItemStack(nameItem(new ItemStack(Material.BARRIER), "&rPrevious Page")), e -> kangarooJumpingMenu.openPowerUpLocationsMenu(arena).open(player, page.previous().getPage())));

		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
