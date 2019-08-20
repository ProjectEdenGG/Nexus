package me.pugabyte.bncore.features.connect4;

import me.pugabyte.bncore.features.connect4.models.Connect4Game;
import org.bukkit.ChatColor;

public class Connect4 {
	public final static String PREFIX = ChatColor.translateAlternateColorCodes('&', "&f[&cConnect&94&f] ");
	Connect4Game game = new Connect4Game();

	public Connect4() {}
}
