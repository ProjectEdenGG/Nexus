package me.pugabyte.bncore.features.connect4;

import org.bukkit.ChatColor;

public class Connect4 {
	public final static String PREFIX = ChatColor.translateAlternateColorCodes('&', "&f[&cConnect&94&f] ");

	public Connect4() {
		new Connect4Command();
	}
}
