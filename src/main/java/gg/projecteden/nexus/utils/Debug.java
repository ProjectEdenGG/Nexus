package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.parchment.HasPlayer;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Debug {

	// Default

	private static boolean DEBUG = false;

	public static boolean isEnabled() {
		return DEBUG;
	}

	public static void setEnabled(boolean debug) {
		Debug.DEBUG = debug;
	}

	public static void log(String message) {
		if (isEnabled())
			Nexus.getInstance().getLogger().info("[DEBUG] " + ChatColor.stripColor(message));
	}

	public static void log(Throwable ex) {
		if (isEnabled())
			ex.printStackTrace();
	}

	public static void log(String message, Throwable ex) {
		log(message);
		log(ex);
	}

	public static void dumpStack() {
		if (isEnabled())
			Thread.dumpStack();
	}

	// Type based

	private static final List<DebugType> ENABLED_DEBUG_TYPES = new ArrayList<>();

	public static boolean isEnabled(DebugType type) {
		return ENABLED_DEBUG_TYPES.contains(type);
	}

	public static void setEnabled(DebugType type, boolean state) {
		if (state)
			ENABLED_DEBUG_TYPES.add(type);
		else
			ENABLED_DEBUG_TYPES.remove(type);
	}

	public static void log(@NotNull DebugType type, String message) {
		if (isEnabled(type))
			Nexus.getInstance().getLogger().info("[DEBUG] " + ChatColor.stripColor(type.prefix() + message));
	}

	public static void log(@NotNull DebugType type, Throwable ex) {
		if (isEnabled(type))
			ex.printStackTrace();
	}

	public static void log(@NotNull DebugType type, String message, Throwable ex) {
		log(type, message);
		log(type, ex);
	}

	public static void dumpStack(@NotNull DebugType type) {
		if (isEnabled(type))
			Thread.dumpStack();
	}

	// Player & type based

	private static final Map<UUID, Set<DebugType>> ENABLED_DEBUGGERS = new HashMap<>();

	public static boolean isEnabled(HasPlayer player, DebugType type) {
		if (player == null || player.getPlayer() == null)
			return false;

		return ENABLED_DEBUGGERS.computeIfAbsent(player.getPlayer().getUniqueId(), $ -> new HashSet<>()).contains(type);
	}

	public static void setEnabled(HasPlayer player, DebugType type, boolean state) {
		if (player == null)
			return;

		var enabledTypes = ENABLED_DEBUGGERS.computeIfAbsent(player.getPlayer().getUniqueId(), $ -> new HashSet<>());

		if (state)
			enabledTypes.add(type);
		else
			enabledTypes.remove(type);
	}

	public static void log(@NotNull HasPlayer player, @NotNull DebugType type, String message) {
		if (isEnabled(player, type))
			PlayerUtils.send(player, type.prefix() + message);
	}

	public static void log(@NotNull HasPlayer player, @NotNull DebugType type, JsonBuilder json) {
		if (isEnabled(player, type))
			new JsonBuilder(type.prefix()).group().next(json).send(player);
	}

	public static void log(@NotNull HasPlayer player, @NotNull DebugType type, Throwable ex) {
		if (isEnabled(player, type))
			PlayerUtils.send(player, ex.getMessage());
	}

	public static void log(@NotNull HasPlayer player, @NotNull DebugType type, String message, Throwable ex) {
		if (isEnabled(player, type)) {
			log(player, type, message);
			log(player, type, ex);
		}
	}

	public static void dumpStack(@NotNull HasPlayer player, @NotNull DebugType type) {
		if (isEnabled(player, type))
			Thread.dumpStack();
	}

	//

	public enum DebugType {
		AUTO_TOOL,
		RESOURCE_PACK,
		RECIPES,
		@Uppercase
		API,
		TITAN,
		ROLE_MANAGER,
		@Uppercase
		JDA,
		DATABASE,
		WORLD_EDIT,
		MINIGAMES,
		CUSTOM_BLOCKS,
		CUSTOM_BLOCK_DAMAGE,
		CUSTOM_BLOCKS_SOUNDS,
		CUSTOM_BLOCKS_JANITOR,
		DECORATION,
		DECORATION_FINDER,
		FLY,
		EFFECTS,
		MISC,
		;

		@SneakyThrows
		public String prefix() {
			var prefix = name();

			if (!getClass().getField(name()).isAnnotationPresent(Uppercase.class))
				prefix = StringUtils.camelCase(this);

			return "[" + prefix + "] ";
		}

		@Target(ElementType.FIELD)
		@Retention(RetentionPolicy.RUNTIME)
		private @interface Uppercase { }
	}

}
