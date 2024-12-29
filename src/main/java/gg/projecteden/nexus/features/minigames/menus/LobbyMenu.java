package gg.projecteden.nexus.features.minigames.menus;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

@Rows(2)
@Title("Lobby Menu")
@RequiredArgsConstructor
public class LobbyMenu extends InventoryProvider {
	private final Arena arena;

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> new LobbyMenu(arena).open(player)));
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(viewer));

		contents.set(1, 2, ClickableItem.of(new ItemBuilder(Material.OAK_DOOR)
				.name("&eLobby Location")
				.lore("&3Current Lobby Location:")
				.lore(MenuUtils.getLocationLore(arena.getLobby().getLocation()))
				.lore("")
				.lore("&eClick to set to current location"),
			e -> {
				arena.getLobby().setLocation(viewer.getLocation());
				arena.write();
				new LobbyMenu(arena).open(viewer);
			}));

		contents.set(1, 6, ClickableItem.of(new ItemBuilder(Material.CLOCK)
				.name("&eWait Time")
				.lore("&3Current Wait Time:", "&e" + arena.getLobby().getWaitTime()),
			e -> openAnvilMenu(viewer, arena, String.valueOf(arena.getLobby().getWaitTime()), (Player p, String text) -> {
				if (Utils.isInt(text)) {
					arena.getLobby().setWaitTime(Integer.parseInt(text));
					arena.write();
					new LobbyMenu(arena).open(viewer);
					return AnvilGUI.Response.text(text);
				} else {
					PlayerUtils.send(viewer, Minigames.PREFIX + "You must use an integer for wait time.");
					return AnvilGUI.Response.close();
				}
			})));
	}

}
