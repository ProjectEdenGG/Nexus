package me.pugabyte.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class ActionBarUtils {

	// Main

	public static void sendActionBar(final Player player, final String message) {
		player.sendActionBar(colorize(message));
	}

	// One player

	public static void sendActionBar(final Player player, ActionBar actionBar) {
		sendActionBar(player, actionBar.getText(), actionBar.getDuration(), actionBar.isFade());
	}

	public static void sendActionBar(final Player player, final String message, int duration) {
		sendActionBar(player, message, duration, true);
	}

	public static void sendActionBar(final Player player, final String message, int duration, boolean fade) {
		sendActionBar(player, message);

		if (!fade && duration >= 0)
			Tasks.wait(duration + 1, () -> sendActionBar(player, ""));

		while (duration > 40)
			Tasks.wait(duration -= 40, () -> sendActionBar(player, message));
	}

	// List of players

	public static void sendActionBar(final List<Player> players, ActionBar actionBar) {
		for (Player player : players)
			sendActionBar(player, actionBar.getText(), actionBar.getDuration(), actionBar.isFade());
	}

	public static void sendActionBar(final List<Player> players, String message) {
		sendActionBar(players, message, -1);
	}

	public static void sendActionBar(final List<Player> players, String message, int duration) {
		sendActionBar(players, message, duration, true);
	}

	public static void sendActionBar(final List<Player> players, String message, int duration, boolean fade) {
		for (Player player : Bukkit.getOnlinePlayers())
			sendActionBar(player, message, duration, fade);
	}

	// All players

	public static void sendActionBarToAllPlayers(ActionBar actionBar) {
		for (Player player : Bukkit.getOnlinePlayers())
			sendActionBar(player, actionBar.getText(), actionBar.getDuration(), actionBar.isFade());
	}

	public static void sendActionBarToAllPlayers(String message) {
		sendActionBarToAllPlayers(message, -1);
	}

	public static void sendActionBarToAllPlayers(String message, int duration) {
		sendActionBarToAllPlayers(message, duration, true);
	}

	public static void sendActionBarToAllPlayers(String message, int duration, boolean fade) {
		for (Player player : Bukkit.getOnlinePlayers())
			sendActionBar(player, message, duration, fade);
	}

	@Data
	@AllArgsConstructor
	public static class ActionBar {
		private String text;
		private int duration;
		private boolean fade;
	}

}
