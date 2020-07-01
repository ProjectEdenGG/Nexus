package me.pugabyte.bncore.features.safecracker.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.safecracker.SafeCrackerEvent;
import me.pugabyte.bncore.models.safecracker.SafeCrackerEventService;
import me.pugabyte.bncore.utils.ItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.HashMap;

public class SafeCrackerGameSelector extends MenuUtils implements InventoryProvider {


	SafeCrackerEventService service = new SafeCrackerEventService();
	SafeCrackerEvent event = service.get();

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> SafeCrackerInventories.openAdminMenu(player));

		contents.set(0, 4, ClickableItem.from(new ItemBuilder(Material.EMERALD_BLOCK).name("&aNew Event").build(), e -> {
			openAnvilMenu(player, "New Game...", (player1, response) -> {
				service.getActiveEvent().setActive(false);
				service.get().getGames().put(response, new SafeCrackerEvent.SafeCrackerGame(response, true, LocalDateTime.now(), "", "", new HashMap<>()));
				service.save(event);
				SafeCrackerInventories.openGameSelectorMenu(player);
				return AnvilGUI.Response.text(response);
			}, (player1) -> SafeCrackerInventories.openGameSelectorMenu(player));
		}));

		int row = 0;
		int column = 8;

		for (SafeCrackerEvent.SafeCrackerGame game : event.getGames().values()) {
			ItemStack item = new ItemBuilder(game.isActive() ? Material.ENCHANTED_BOOK : Material.BOOK).name("&e" + game.getName())
					.lore("&7Click to set me as").lore("&7the active game").build();
			contents.set(row, column, ClickableItem.from(item, e -> {
				service.setActiveGame(game);
				SafeCrackerInventories.openGameSelectorMenu(player);
			}));
		}

	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
