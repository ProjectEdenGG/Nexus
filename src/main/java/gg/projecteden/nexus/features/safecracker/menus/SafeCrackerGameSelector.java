package gg.projecteden.nexus.features.safecracker.menus;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEvent;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEventService;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.HashMap;

@Disabled
@Title("SafeCracker Game Selector")
public class SafeCrackerGameSelector extends InventoryProvider {
	private final SafeCrackerEventService service = new SafeCrackerEventService();
	private final SafeCrackerEvent event = service.get0();

	@Override
	public void init() {
		addBackItem(e -> new SafeCrackerAdminProvider().open(viewer));

		contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK).name("&aNew Event").build(), e -> {
			MenuUtils.openAnvilMenu(viewer, "New Game...", (player1, response) -> {
				service.getActiveEvent().setActive(false);
				service.get0().getGames().put(response, new SafeCrackerEvent.SafeCrackerGame(response, true, LocalDateTime.now(), "", "", new HashMap<>()));
				service.save(event);
				new SafeCrackerGameSelector().open(viewer);

				return AnvilGUI.Response.text(response);
			}, (player1) -> new SafeCrackerGameSelector().open(viewer));
		}));

		int row = 0;
		int column = 8;

		for (SafeCrackerEvent.SafeCrackerGame game : event.getGames().values()) {
			ItemStack item = new ItemBuilder(game.isActive() ? Material.ENCHANTED_BOOK : Material.BOOK).name("&e" + game.getName())
					.lore("&7Click to set me as").lore("&7the active game").build();
			contents.set(row, column, ClickableItem.of(item, e -> {
				service.setActiveGame(game);
				new SafeCrackerGameSelector().open(viewer);

			}));
		}

	}
}
