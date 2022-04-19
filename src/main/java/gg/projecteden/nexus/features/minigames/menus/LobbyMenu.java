package gg.projecteden.nexus.features.minigames.menus;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.RequiredArgsConstructor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

import static gg.projecteden.nexus.features.menus.MenuUtils.getLocationLore;
import static gg.projecteden.nexus.features.minigames.Minigames.PREFIX;

@RequiredArgsConstructor
public class LobbyMenu extends InventoryProvider {
	private final Arena arena;

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
			.provider(this)
			.title("Lobby Menu")
			.rows(2)
			.build()
			.open(player, page);
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> new LobbyMenu(arena).open(player)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> new ArenaMenu(arena).open(player));

		contents.set(1, 2, ClickableItem.of(new ItemBuilder(Material.OAK_DOOR)
				.name("&eLobby Location")
				.lore("&3Current Lobby Location:")
				.lore(getLocationLore(arena.getLobby().getLocation()))
				.lore("")
				.lore("&eClick to set to current location"),
			e -> {
				arena.getLobby().setLocation(player.getLocation());
				arena.write();
				new LobbyMenu(arena).open(player);
			}));

		contents.set(1, 6, ClickableItem.of(new ItemBuilder(Material.CLOCK)
				.name("&eWait Time")
				.lore("&3Current Wait Time:", "&e" + arena.getLobby().getWaitTime()),
			e -> openAnvilMenu(player, arena, String.valueOf(arena.getLobby().getWaitTime()), (Player p, String text) -> {
				if (Utils.isInt(text)) {
					arena.getLobby().setWaitTime(Integer.parseInt(text));
					arena.write();
					new LobbyMenu(arena).open(player);
					return AnvilGUI.Response.text(text);
				} else {
					PlayerUtils.send(player, PREFIX + "You must use an integer for wait time.");
					return AnvilGUI.Response.close();
				}
			})));
	}

}
