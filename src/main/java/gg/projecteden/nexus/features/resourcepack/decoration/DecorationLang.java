package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.models.cooldown.CooldownService;
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
			return !new CooldownService().check(player, this.key, ticks);
		}

		public boolean isOnCooldown(Player player, UUID uuid) {
			return isOnCooldown(player, uuid, this.ticks);
		}

		public boolean isOnCooldown(Player player, UUID uuid, long ticks) {
			return !new CooldownService().check(player, this.key + "-" + uuid, ticks);
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

	public static void debug(String message) {
		debug(false, message);
	}

	public static void debug(boolean deep, String message) {
		for (DebuggerData debugger : debuggers) {
			debugger.send(deep, message);
		}
	}

	public static void debug(Player player, String message) {
		debug(player, false, message);
	}

	public static void debug(Player player, boolean deep, String message) {
		if (player == null)
			return;

		DebuggerData data = getDebuggerData(player.getUniqueId());
		if (data != null)
			data.send(deep, message);
	}

	public static void debug(Player player, Runnable runnable) {
		if (player == null)
			return;

		DebuggerData data = getDebuggerData(player.getUniqueId());
		if (data != null)
			data.run(runnable);
	}

	@AllArgsConstructor
	private static class DebuggerData {
		@Getter
		private UUID uuid;
		@Getter
		private boolean deep;

		public void send(boolean deep, String message) {
			if (deep) {
				if (this.deep)
					PlayerUtils.send(uuid, message);
				else
					return;
			}

			PlayerUtils.send(uuid, message);
		}

		public void run(Runnable runnable) {
			if (!this.deep)
				return;

			runnable.run();
		}
	}


}
