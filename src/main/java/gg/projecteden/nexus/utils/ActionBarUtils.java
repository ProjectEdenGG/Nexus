package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.parchment.HasPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

public class ActionBarUtils {

	private static final Map<UUID, Set<Integer>> playerTaskIds = new HashMap<>();

	// Main

	private static void sendActionBarForReal(final HasPlayer player, final String message) {
		player.getPlayer().sendActionBar(colorize(message));
	}

	// One player

	public static void sendActionBar(final HasPlayer player, final String message) {
		sendActionBar(player, colorize(message), -1);
	}

	public static void sendActionBar(final HasPlayer player, ActionBar actionBar) {
		sendActionBar(player, actionBar.getText(), actionBar.getDuration(), actionBar.isFade());
	}

	public static void sendActionBar(final HasPlayer player, final String message, long duration) {
		sendActionBar(player, message, duration, true);
	}

	public static void sendActionBar(final HasPlayer player, final String message, long duration, boolean fade) {
		if (player == null)
			return;

		Set<Integer> taskIds = playerTaskIds.getOrDefault(player.getPlayer().getUniqueId(), new HashSet<>());
		Tasks.cancel(taskIds);
		taskIds.clear();

		sendActionBarForReal(player, message);

		if (!fade && duration >= 0)
			taskIds.add(Tasks.wait(duration + 1, () -> sendActionBarForReal(player, "")));

		while (duration > 40)
			taskIds.add(Tasks.wait(duration -= 40, () -> sendActionBarForReal(player, message)));

		playerTaskIds.put(player.getPlayer().getUniqueId(), taskIds);
	}

	// List of players

	public static void sendActionBar(final List<? extends HasPlayer> players, ActionBar actionBar) {
		for (HasPlayer player : players)
			sendActionBar(player, actionBar.getText(), actionBar.getDuration(), actionBar.isFade());
	}

	public static void sendActionBar(final List<? extends HasPlayer> players, String message) {
		sendActionBar(players, message, -1);
	}

	public static void sendActionBar(final List<? extends HasPlayer> players, String message, long duration) {
		sendActionBar(players, message, duration, true);
	}

	public static void sendActionBar(final List<? extends HasPlayer> players, String message, long duration, boolean fade) {
		for (HasPlayer player : players)
			sendActionBar(player, message, duration, fade);
	}

	// All players

	public static void sendActionBarToAllPlayers(ActionBar actionBar) {
		for (Player player : OnlinePlayers.getAll())
			sendActionBar(player, actionBar.getText(), actionBar.getDuration(), actionBar.isFade());
	}

	public static void sendActionBarToAllPlayers(String message) {
		sendActionBarToAllPlayers(message, -1);
	}

	public static void sendActionBarToAllPlayers(String message, long duration) {
		sendActionBarToAllPlayers(message, duration, true);
	}

	public static void sendActionBarToAllPlayers(String message, long duration, boolean fade) {
		for (Player player : OnlinePlayers.getAll())
			sendActionBar(player, message, duration, fade);
	}

	@Data
	@AllArgsConstructor
	public static class ActionBar {
		private String text;
		private long duration;
		private boolean fade;
	}
}
