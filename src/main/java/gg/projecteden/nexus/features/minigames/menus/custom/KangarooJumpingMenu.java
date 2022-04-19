package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.features.menus.api.content.SlotIterator;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.KangarooJumping;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.KangarooJumpingArena;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@CustomMechanicSettings(KangarooJumping.class)
public class KangarooJumpingMenu extends MenuUtils implements InventoryProvider {

	KangarooJumpingArena arena;

	public KangarooJumpingMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, KangarooJumpingArena.class);
	}

	public SmartInventory openPowerUpLocationsMenu(Arena arena){
		SmartInventory INV = SmartInventory.builder()
				.id("powerUpLocationsMenu")
				.maxSize()
				.provider(new KangarooJumpingSubMenu(arena))
				.title("Power Up Locations Menu")
				.build();
		return INV;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.of(backItem(), e -> Minigames.menus.openArenaMenu(player, arena)));

		contents.set(1, 0, ClickableItem.of(new ItemBuilder(Material.POTION).name("&ePower Up Locations"),
				e -> openPowerUpLocationsMenu(arena).open(player)));
	}

	public static class KangarooJumpingSubMenu extends MenuUtils implements InventoryProvider {
		KangarooJumpingArena arena;

		public KangarooJumpingSubMenu(@NonNull Arena arena) {
			this.arena = ArenaManager.convert(arena, KangarooJumpingArena.class);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			KangarooJumpingMenu kangarooJumpingMenu = new KangarooJumpingMenu(arena);

			addBackItem(contents, e -> Minigames.menus.openArenaMenu(player, arena));

			Pagination page = contents.pagination();

			contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK)
						.name("&eAdd Power Up Location")
					.lore("&3Click to add a Power Up", "&3at your current location."),
				e -> {
					arena.getPowerUpLocations().add(player.getLocation());
					arena.write();
					kangarooJumpingMenu.openPowerUpLocationsMenu(arena).open(player, page.getPage());
				}));

			ItemBuilder deleteItem = new ItemBuilder(Material.TNT)
				.name("&cDelete Item")
				.lore("&7Click me to enter deletion mode.", "&7Then, click a power up location with", "&7me to delete the location.");
			contents.set(0, 8, ClickableItem.of(deleteItem, e -> Tasks.wait(2, () -> {
				if (player.getItemOnCursor().getType().equals(Material.TNT)) {
					player.setItemOnCursor(new ItemStack(Material.AIR));
				} else if (isNullOrAir(player.getItemOnCursor())) {
					player.setItemOnCursor(deleteItem.build());
				}
			})));

			if (arena.getPowerUpLocations() == null) return;

			ClickableItem[] clickableItems = new ClickableItem[arena.getPowerUpLocations().size()];
			List<Location> powerUpLocations = new ArrayList<>(arena.getPowerUpLocations());
			for (int i = 0; i < powerUpLocations.size(); i++) {
				Location powerUpLocation = powerUpLocations.get(i);
				ItemBuilder item = new ItemBuilder(Material.COMPASS).name("&ePower Up #" + (i + 1))
					.lore(getLocationLore(powerUpLocations.get(i)))
					.lore("", "&7Click to Teleport");

				clickableItems[i] = ClickableItem.of(item, e -> {
					if (player.getItemOnCursor().getType().equals(Material.TNT)) {
						Tasks.wait(2, () -> {
							arena.getPowerUpLocations().remove(powerUpLocation);
							arena.write();
							player.setItemOnCursor(new ItemStack(Material.AIR));
							kangarooJumpingMenu.openPowerUpLocationsMenu(arena).open(player, page.getPage());
						});
					} else {
						player.teleportAsync(powerUpLocation);
					}
				});
			}

			page.setItems(clickableItems);
			page.setItemsPerPage(36);
			page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

			if (!page.isLast())
				contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.ARROW).name("&fNext Page"), e -> kangarooJumpingMenu.openPowerUpLocationsMenu(arena).open(player, page.next().getPage())));
			if (!page.isFirst())
				contents.set(0, 7, ClickableItem.of(new ItemBuilder(Material.BARRIER).name("&fPrevious Page"), e -> kangarooJumpingMenu.openPowerUpLocationsMenu(arena).open(player, page.previous().getPage())));
		}

	}
}
