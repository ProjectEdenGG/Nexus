package me.pugabyte.bncore.features.holidays.pugmas20.menu;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.features.holidays.pugmas20.menu.providers.AdventProvider;
import org.bukkit.entity.Player;

public class AdventMenu {
	public static void openAdvent(Player player) {
		SmartInventory.builder()
				.title("Advent")
				.size(6, 9)
				.provider(new AdventProvider())
				.build()
				.open(player);
	}
}
