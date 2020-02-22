package me.pugabyte.bncore.features.warps;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.features.warps.providers.WarpsMenuProvider;
import org.bukkit.ChatColor;
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
