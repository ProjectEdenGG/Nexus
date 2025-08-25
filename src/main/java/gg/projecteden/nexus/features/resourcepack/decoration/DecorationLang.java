package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DecorationLang {
	@Getter
	private static final String PREFIX = StringUtils.getPrefix("Decoration");

	@AllArgsConstructor
	public enum DecorationError {
		LOCKED(PREFIX + "&cThis decoration is locked"),
		SEAT_OCCUPIED(PREFIX + "&cSeat is occupied"),
		IMPROPER_PLACEMENT(PREFIX + "&cYou cannot place this decoration in this way"),
		LACKING_FUNDS(PREFIX + "&cYou don't have enough money to buy this"),

		// Special
		UNKNOWN_TARGET_DECORATION("&CYou are not looking at a decoration!"),
		DISABLED(PREFIX + "&cThis feature has temporarily been disabled"),
		WORLDGUARD_USAGE("&c&lHey! &7Sorry, but you can't use that here."),
		UNRELEASED_FEATURE(PREFIX + "&cYou cannot use this feature yet"),
		CONTACT_ADMINS(PREFIX + "&cContact an admin if you see this message"),
		;

		@Getter
		final String message;

		public void send(Player player) {
			send(player, null);
		}

		public void send(Player player, String extra) {
			if (player == null || !player.isOnline())
				return;

			String message = this.message;
			if (extra != null)
				message += extra;

			sendFinal(player, message);
		}

		public void sendCustom(Player player, @Nullable String message) {
			if (player == null || !player.isOnline())
				return;

			if (message == null)
				message = this.message;

			sendFinal(player, message);
		}

		private void sendFinal(Player player, String message) {
			PlayerUtils.send(player, message);
		}
	}

	public static final int DEFAULT_COOLDOWN_TICKS = 5;

	@AllArgsConstructor
	public enum DecorationCooldown {
		LOCKED("decoration-locked"),
		INTERACT("decoration-interact"),
		PLACE("decoration-place"),
		DESTROY("decoration-destroy"),
		IMPROPER_PLACEMENT("decoration-improper-place");

		private final String key;
		private final long ticks = TickTime.TICK.x(DEFAULT_COOLDOWN_TICKS);

		public boolean isOnCooldown(Player player) {
			return isOnCooldown(player, this.ticks);
		}

		public boolean isOnCooldown(Player player, long ticks) {
			return CooldownService.isOnCooldown(player, this.key, ticks);
		}

		public boolean isOnCooldown(Player player, UUID uuid) {
			return isOnCooldown(player, uuid, this.ticks);
		}

		public boolean isOnCooldown(Player player, UUID uuid, long ticks) {
			return CooldownService.isOnCooldown(player, this.key + "-" + uuid, ticks);
		}
	}

	private static final Set<DebuggerData> debuggers = new HashSet<>();

	public static void startDebugging(UUID uuid, boolean deep) {
		if (isDebugging(uuid))
			return;

		debuggers.add(new DebuggerData(uuid, deep));
	}

	public static void stopDebugging(UUID uuid) {
		debuggers.remove(getDebuggerData(uuid));
	}

	public static boolean isDebugging(UUID uuid) {
		return getDebuggerData(uuid) != null;
	}

	public static @Nullable DebuggerData getDebuggerData(UUID uuid) {
		if (uuid == null)
			return null;

		for (DebuggerData debugger : debuggers) {
			if (debugger.getUuid().equals(uuid)) {
				return debugger;
			}
		}

		return null;
	}

	public static List<UUID> getDebuggerUUIDs() {
		List<UUID> result = new ArrayList<>();
		for (DebuggerData debugger : debuggers) {
			result.add(debugger.getUuid());
		}
		return result;
	}

	public static void debug(Player player, String message) {
		_debug(player, false, new JsonBuilder(message));
	}

	public static void debug(Player player, JsonBuilder json) {
		_debug(player, false, json);
	}

	public static void deepDebug(Player player, String message) {
		_debug(player, true, new JsonBuilder(message));
	}

	private static void _debug(Player player, boolean deep, JsonBuilder json) {
		if (player == null)
			return;

		DebuggerData data = getDebuggerData(player.getUniqueId());
		if (data != null)
			data.send(deep, json);
	}

	// Runnable

	public static void debug(Player player, Runnable runnable) {
		if (player == null)
			return;

		DebuggerData data = getDebuggerData(player.getUniqueId());
		if (data != null)
			data.run(runnable);
	}

	// All debuggers

	public static void debug(String message) {
		debug(false, message);
	}

	public static void debug(JsonBuilder json) {
		debug(false, json);
	}

	public static void debug(boolean deep, String message) {
		debug(deep, new JsonBuilder(message));
	}

	public static void debug(boolean deep, JsonBuilder json) {
		for (DebuggerData debugger : debuggers) {
			debugger.send(deep, json);
		}
	}

	@AllArgsConstructor
	private static class DebuggerData {
		@Getter
		private UUID uuid;
		@Getter
		private boolean deep;

		public void send(boolean isDeep, JsonBuilder json) {
			if (isDeep) {
				if (!this.deep)
					return;

				PlayerUtils.send(uuid, json);
				return;
			}

			PlayerUtils.send(uuid, json);
		}

		public void run(Runnable runnable) {
			if (!this.deep)
				return;

			runnable.run();
		}
	}


}
