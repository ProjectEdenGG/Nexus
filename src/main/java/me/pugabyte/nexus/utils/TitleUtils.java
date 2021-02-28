package me.pugabyte.nexus.utils;


import com.destroystokyo.paper.Title;
import org.bukkit.entity.Player;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class TitleUtils {

	public static void sendSubtitle(Player player, String subtitle) {
		sendTitle(player, null, subtitle);
	}

	public static void sendTitle(Player player, String title) {
		sendTitle(player, title, null);
	}

	public static void sendTitle(Player player, String title, String subtitle) {
		sendTitle(player, title, subtitle, 20, 200, 20);
	}

	public static void sendSubtitle(Player player, String subtitle, int fadeIn, int stay, int fadeOut) {
		sendTitle(player, null, subtitle, fadeIn, stay, fadeOut);
	}

	public static void sendTitle(Player player, String title, int fadeIn, int stay, int fadeOut) {
		sendTitle(player, title, null, fadeIn, stay, fadeOut);
	}

	public static void sendTitle(final Player player, final String title, final String subtitle, int fadeIn, int stay, int fadeOut) {
		player.sendTitle(new Title(colorize(title), colorize(subtitle), fadeIn, stay, fadeOut));
	}
}
