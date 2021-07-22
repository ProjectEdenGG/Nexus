package gg.projecteden.nexus.features.minigames.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.NonNull;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

import static gg.projecteden.nexus.features.minigames.Minigames.PREFIX;
import static gg.projecteden.nexus.features.minigames.Minigames.menus;

public class LobbyMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public LobbyMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> menus.openLobbyMenu(player, arena)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> menus.openArenaMenu(player, arena));

		contents.set(1, 2, ClickableItem.from(nameItem(
				Material.OAK_DOOR,
				"&eLobby Location",
				"&3Current Lobby Location:" + "||" + getLocationLore(arena.getLobby().getLocation()) + "|| ||&eClick to set to current location"
			),
			e -> {
				arena.getLobby().setLocation(player.getLocation());
				arena.write();
				menus.openLobbyMenu(player, arena);
			}));

		contents.set(1, 6, ClickableItem.from(nameItem(
				Material.CLOCK,
				"&eWait Time",
				"&3Current Wait Time:||&e" + arena.getLobby().getWaitTime()
			),
			e -> openAnvilMenu(player, arena, String.valueOf(arena.getLobby().getWaitTime()), (Player p, String text) -> {
				if (Utils.isInt(text)) {
					arena.getLobby().setWaitTime(Integer.parseInt(text));
					arena.write();
					menus.openLobbyMenu(player, arena);
					return AnvilGUI.Response.text(text);
				} else {
					PlayerUtils.send(player, PREFIX + "You must use an integer for wait time.");
					return AnvilGUI.Response.close();
				}
			})));
	}

}
