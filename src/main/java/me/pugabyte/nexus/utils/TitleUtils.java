package me.pugabyte.nexus.utils;


import org.bukkit.entity.Player;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class TitleUtils {

	// Title only

	public static void sendTitle(Player player, String title) {
		sendTitle(player, title, "");
	}

	public static void sendTitle(Player player, String title, int stay, int fade) {
		sendTitle(player, title, "", fade, stay, fade);
	}

	public static void sendTitle(Player player, String title, int stay) {
		sendTitle(player, title, "", 20, stay, 20);
	}

	public static void sendTitle(Player player, String title, int fadeIn, int stay, int fadeOut) {
		sendTitle(player, title, "", fadeIn, stay, fadeOut);
	}

	// Subtitle Only

	public static void sendSubtitle(Player player, String subtitle) {
		sendTitle(player, "", subtitle);
	}

	public static void sendSubtitle(Player player, String subtitle, int stay) {
		sendTitle(player, "", subtitle, 20, stay, 20);
	}

	public static void sendSubtitle(Player player, String subtitle, int stay, int fade) {
		sendTitle(player, "", subtitle, fade, stay, fade);
	}

	public static void sendSubtitle(Player player, String subtitle, int fadeIn, int stay, int fadeOut) {
		sendTitle(player, "", subtitle, fadeIn, stay, fadeOut);
	}

	// Both Titles

	public static void sendTitle(Player player, String title, String subtitle) {
		sendTitle(player, title, subtitle, 20, 200, 20);
	}

	public static void sendTitle(Player player, String title, String subtitle, int stay) {
		sendTitle(player, title, subtitle, 20, stay, 20);
	}

	public static void sendTitle(Player player, String title, String subtitle, int stay, int fade) {
		sendTitle(player, title, subtitle, fade, stay, fade);
	}

	// Main

	public static void sendTitle(final Player player, final String title, final String subtitle, int fadeIn, int stay, int fadeOut) {
		player.sendMessage("Title: " + title + " | " + subtitle + " | " + fadeIn + " | " + stay + " | " + fadeOut);
		player.sendTitle(colorize(title), colorize(subtitle), fadeIn, stay, fadeOut);

	}
}
