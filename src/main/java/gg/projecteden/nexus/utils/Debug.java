package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

public class Debug {

	@Getter
	@Setter
	private static boolean debug = false;
	@Getter
	private static List<DebugType> ENABLED_DEBUG_TYPES = new ArrayList<>();

	public static boolean isEnabled() {
		return debug;
	}

	public static boolean isEnabled(DebugType type) {
		return ENABLED_DEBUG_TYPES.contains(type);
	}

	public static void enable(DebugType type) {
		ENABLED_DEBUG_TYPES.add(type);
	}

	public static void disable(DebugType type) {
		ENABLED_DEBUG_TYPES.remove(type);
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
	}

}
