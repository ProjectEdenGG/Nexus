package gg.projecteden.nexus.features.minigames.menus;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static gg.projecteden.nexus.features.minigames.Minigames.menus;

public class DeleteArenaMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public DeleteArenaMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		ItemBuilder cancelItem = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("&7Cancel");
		contents.fillRect(0, 0, 2, 8, ClickableItem.of(cancelItem, e -> menus.openArenaMenu(player, arena)));
		contents.fillRect(1, 1, 1, 7, ClickableItem.of(cancelItem, e -> menus.openArenaMenu(player, arena)));

		contents.set(1, 4, ClickableItem.of(Material.TNT, "&4&lDELETE ARENA", "&7This cannot be undone.",
				e -> {
					arena.delete();
					PlayerUtils.send(player, Minigames.PREFIX + "Arena &e" + arena.getName() + " &3deleted");
				}));
	}

}
