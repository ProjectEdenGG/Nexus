package me.pugabyte.nexus.utils;


import me.lexikiq.HasPlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class TitleUtils {

	// Title only

	public static void sendTitle(HasPlayer player, String title) {
		sendTitle(player, title, "");
	}

	public static void sendTitle(HasPlayer player, String title, int stay, int fade) {
		sendTitle(player, title, "", fade, stay, fade);
	}

	public static void sendTitle(HasPlayer player, String title, int stay) {
		sendTitle(player, title, "", 20, stay, 20);
	}

	public static void sendTitle(HasPlayer player, String title, int fadeIn, int stay, int fadeOut) {
		sendTitle(player, title, "", fadeIn, stay, fadeOut);
	}

	// Subtitle Only

	public static void sendSubtitle(HasPlayer player, String subtitle) {
		sendTitle(player, "", subtitle);
	}

	public static void sendSubtitle(HasPlayer player, String subtitle, int stay) {
		sendTitle(player, "", subtitle, 20, stay, 20);
	}

	public static void sendSubtitle(HasPlayer player, String subtitle, int stay, int fade) {
		sendTitle(player, "", subtitle, fade, stay, fade);
	}

	public static void sendSubtitle(HasPlayer player, String subtitle, int fadeIn, int stay, int fadeOut) {
		sendTitle(player, "", subtitle, fadeIn, stay, fadeOut);
	}

	// Both Titles

	public static void sendTitle(HasPlayer player, String title, String subtitle) {
		sendTitle(player, title, subtitle, 20, 200, 20);
	}

	public static void sendTitle(HasPlayer player, String title, String subtitle, int stay) {
		sendTitle(player, title, subtitle, 20, stay, 20);
	}

	public static void sendTitle(HasPlayer player, String title, String subtitle, int stay, int fade) {
		sendTitle(player, title, subtitle, fade, stay, fade);
	}

	// Main

	public static void sendTitle(final HasPlayer player, final String title, final String subtitle, int fadeIn, int stay, int fadeOut) {
		player.getPlayer().sendTitle(colorize(title), colorize(subtitle), fadeIn, stay, fadeOut);
	}

	// All Players

	public static void sendTitleToAllPlayers(String title, String subtitle) {
		sendTitleToAllPlayers(title, subtitle, 20, 200, 20);
	}

	public static void sendTitleToAllPlayers(String title, String subtitle, int stay) {
		sendTitleToAllPlayers(title, subtitle, 20, stay, 20);
	}

	public static void sendTitleToAllPlayers(String title, String subtitle, int stay, int fade) {
		sendTitleToAllPlayers(title, subtitle, fade, stay, fade);
	}

	public static void sendTitleToAllPlayers(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
		}
	}

	// adventure

	public static Duration durationOf(long ticks) {
		return Duration.ofSeconds(ticks).dividedBy(20);
	}

	// both

	public static void sendTitle(final Audience player, final ComponentLike title, final ComponentLike subtitle) {
		sendTitle(player, title, subtitle, 200);
	}

	public static void sendTitle(final Audience player, final ComponentLike title, final ComponentLike subtitle, final int stay) {
		sendTitle(player, title, subtitle, stay, 20);
	}

	public static void sendTitle(final Audience player, final ComponentLike title, final ComponentLike subtitle, final int stay, final int fade) {
		sendTitle(player, title, subtitle, fade, stay, fade);
	}

	public static void sendTitle(final Audience player, final ComponentLike title, final ComponentLike subtitle, final int fadeIn, final int stay, final int fadeOut) {
		sendTitle(player, title, subtitle, durationOf(fadeIn), durationOf(stay), durationOf(fadeOut));
	}

	public static void sendTitle(final Audience player, final ComponentLike title, final ComponentLike subtitle, final Duration fadeIn, final Duration stay, final Duration fadeOut) {
		sendTitle(player, title, subtitle, Title.Times.of(fadeIn, stay, fadeOut));
	}

	public static void sendTitle(final Audience player, final ComponentLike title, final ComponentLike subtitle, final Title.Times times) {
		sendTitle(player, Title.title(title.asComponent(), subtitle.asComponent(), times));
	}

	public static void sendTitle(final Audience player, final Title title) {
		player.showTitle(title);
	}

	// title

	public static void sendTitle(final Audience player, final ComponentLike title) {
		sendTitle(player, title, 200);
	}

	public static void sendTitle(final Audience player, final ComponentLike title, final int stay) {
		sendTitle(player, title, stay, 20);
	}

	public static void sendTitle(final Audience player, final ComponentLike title, final int stay, final int fade) {
		sendTitle(player, title, fade, stay, fade);
	}

	public static void sendTitle(final Audience player, final ComponentLike title, final int fadeIn, final int stay, final int fadeOut) {
		sendTitle(player, title, durationOf(fadeIn), durationOf(stay), durationOf(fadeOut));
	}

	public static void sendTitle(final Audience player, final ComponentLike title, final Duration fadeIn, final Duration stay, final Duration fadeOut) {
		sendTitle(player, title, Title.Times.of(fadeIn, stay, fadeOut));
	}

	public static void sendTitle(final Audience player, final ComponentLike title, final Title.Times times) {
		sendTitle(player, Title.title(title.asComponent(), Component.empty(), times));
	}

	// subtitle

	public static void sendSubtitle(final Audience player, final ComponentLike subtitle) {
		sendSubtitle(player, subtitle, 200);
	}

	public static void sendSubtitle(final Audience player, final ComponentLike subtitle, final int stay) {
		sendSubtitle(player, subtitle, stay, 20);
	}

	public static void sendSubtitle(final Audience player, final ComponentLike subtitle, final int stay, final int fade) {
		sendSubtitle(player, subtitle, fade, stay, fade);
	}

	public static void sendSubtitle(final Audience player, final ComponentLike subtitle, final int fadeIn, final int stay, final int fadeOut) {
		sendSubtitle(player, subtitle, durationOf(fadeIn), durationOf(stay), durationOf(fadeOut));
	}

	public static void sendSubtitle(final Audience player, final ComponentLike subtitle, final Duration fadeIn, final Duration stay, final Duration fadeOut) {
		sendSubtitle(player, subtitle, Title.Times.of(fadeIn, stay, fadeOut));
	}

	public static void sendSubtitle(final Audience player, final ComponentLike subtitle, final Title.Times times) {
		sendTitle(player, Title.title(Component.empty(), subtitle.asComponent(), times));
	}
}
