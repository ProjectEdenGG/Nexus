package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

import static me.pugabyte.bncore.features.minigames.Minigames.PREFIX;
import static me.pugabyte.bncore.features.minigames.Minigames.menus;

public class LobbyMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public LobbyMenu(Arena arena) {
		this.arena = arena;
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		openAnvilMenu(player, text, onComplete, p -> Utils.wait(1, () -> menus.openLobbyMenu(player, arena)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> menus.openArenaMenu(player, arena));

		contents.set(1, 2, ClickableItem.from(nameItem(
				Material.WOOD_DOOR,
				"&eLobby Location",
				"&3Current Lobby Location:" + "||" + getLocationLore(arena.getLobby().getLocation()) + "|| ||&eClick to set to current location"
			),
			e -> {
				arena.getLobby().setLocation(player.getLocation());
				arena.write();
				menus.openLobbyMenu(player, arena);
			}));

		contents.set(1, 6, ClickableItem.from(nameItem(
				Material.WATCH,
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
					player.sendMessage(Utils.colorize(PREFIX + "You must use an integer for wait time."));
					return AnvilGUI.Response.close();
				}
			})));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
