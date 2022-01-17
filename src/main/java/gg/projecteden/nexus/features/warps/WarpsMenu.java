package gg.projecteden.nexus.features.warps;

import fr.minuskube.inv.SmartInventory;
import gg.projecteden.nexus.features.warps.providers.WarpsMenuProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class WarpsMenu {
	public static void open(Player player, WarpMenu menu) {
		SmartInventory inv = SmartInventory.builder()
				.provider(new WarpsMenuProvider(menu))
				.size(menu.getSize(), 9)
				.title(ChatColor.DARK_AQUA + "Warps")
				.build();

		inv.open(player);

	}
}
