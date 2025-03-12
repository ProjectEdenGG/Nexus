package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

public class Debug {

	private static boolean debug = false;
	private static final Map<UUID, Set<DebugType>> debuggers = new HashMap<>();
	private static List<DebugType> ENABLED_DEBUG_TYPES = new ArrayList<>();

	public static boolean isEnabled() {
		return debug;
	}

	public static boolean isEnabled(DebugType type) {
		return ENABLED_DEBUG_TYPES.contains(type);
	}

	public static boolean isEnabled(Player player, DebugType type) {
		if (player == null)
			return false;

		if (!debuggers.containsKey(player.getUniqueId()))
			debuggers.put(player.getUniqueId(), new HashSet<>());

		return debuggers.get(player.getUniqueId()).contains(type);
	}

	public static void setEnabled(boolean debug) {
		Debug.debug = debug;
	}

	public static void setEnabled(DebugType type, boolean state) {
		if (state)
			ENABLED_DEBUG_TYPES.add(type);
		else
			ENABLED_DEBUG_TYPES.remove(type);
	}

	public static void setEnabled(Player player, DebugType type, boolean state) {
		if (player == null)
			return;

		if (!debuggers.containsKey(player.getUniqueId()))
			debuggers.put(player.getUniqueId(), new HashSet<>());

		if (state)
			debuggers.get(player.getUniqueId()).add(type);
		else
			debuggers.get(player.getUniqueId()).remove(type);
	}

	public static void log(String message) {
		if (debug)
			Nexus.getInstance().getLogger().info("[DEBUG] " + ChatColor.stripColor(message));
	}

	public static void log(Throwable ex) {
		if (debug)
			ex.printStackTrace();
	}

	public static void log(String message, Throwable ex) {
		log(message);
		log(ex);
	}

	public static void log(DebugType type, String message) {
		if (isEnabled(type))
			log("[" + camelCase(type) + "] " + message);
	}

	public static void log(DebugType type, Throwable ex) {
		if (isEnabled(type))
			log(ex);
	}

	public static void log(DebugType type, String message, Throwable ex) {
		if (isEnabled(type))
			log("[" + camelCase(type) + "] " + message, ex);
	}

	// per-player

	public static void log(Player player, DebugType type, String message, Throwable ex) {
		if (isEnabled(player, type)) {
			log(player, type, message);
			log(player, type, ex);
		}
	}

	public static void log(Player player, DebugType type, String message) {
		if (isEnabled(player, type))
			PlayerUtils.send(player, "[" + camelCase(type) + "] " + message);
	}

	public static void log(Player player, DebugType type, JsonBuilder json) {
		if (isEnabled(player, type))
			new JsonBuilder("[" + camelCase(type) + "] ").group().next(json).send(player);
	}

	public static void log(Player player, DebugType type, Throwable ex) {
		if (isEnabled(player, type))
			PlayerUtils.send(player, ex.getMessage());
	}

	//

	public static void dumpStack() {
		if (!debug)
			return;

		Thread.dumpStack();
	}

	public enum DebugType {
		RESOURCE_PACK,
		RECIPES,
		API,
		TITAN,
		ROLE_MANAGER,
		JDA,
		DATABASE,
		WORLD_EDIT,
		BLOCK_DAMAGE,
		MINIGAMES,
		CUSTOM_BLOCKS,
		CUSTOM_BLOCKS_JANITOR,
	}

}
