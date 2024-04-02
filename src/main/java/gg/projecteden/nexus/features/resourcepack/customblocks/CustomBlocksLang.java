package gg.projecteden.nexus.features.resourcepack.customblocks;

import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CustomBlocksLang {
	private static final Set<UUID> debuggers = new HashSet<>();

	public static boolean isDebugging(UUID uuid) {
		return debuggers.contains(uuid);
	}

	public static void startDebugging(UUID uuid) {
		if (isDebugging(uuid))
			return;

		debuggers.add(uuid);
	}

	public static void stopDebugging(UUID uuid) {
		debuggers.remove(uuid);
	}

	public static void debug(String message) {
		for (Dev dev : List.of(Dev.WAKKA, Dev.GRIFFIN)) {
			debug(dev.getPlayer(), message);
		}
	}

	public static void debug(Player player, String message) {
		if (player == null)
			return;

		if (!isDebugging(player.getUniqueId()))
			return;

		PlayerUtils.send(player, message);
	}

	public static void debug(JsonBuilder jsonBuilder) {
		for (Dev dev : List.of(Dev.WAKKA, Dev.GRIFFIN)) {
			debug(dev.getPlayer(), jsonBuilder);
		}
	}

	public static void debug(Player player, JsonBuilder jsonBuilder) {
		if (player == null)
			return;

		if (!isDebugging(player.getUniqueId()))
			return;

		jsonBuilder.send(player);
	}
}
