package gg.projecteden.nexus.features.warps;

import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.warps.providers.WarpsMenuProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class WarpsMenu {
	public static void open(Player player, WarpMenu menu) {
		SmartInventory inv = SmartInventory.builder()
				.provider(new WarpsMenuProvider(menu))
				.rows(menu.getSize())
				.title(ChatColor.DARK_AQUA + "Warps")
				.build();

		inv.open(player);

	}
}
